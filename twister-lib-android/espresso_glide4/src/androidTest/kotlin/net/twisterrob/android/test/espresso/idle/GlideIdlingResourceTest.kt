package net.twisterrob.android.test.espresso.idle

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.test.core.app.launchActivity
import androidx.test.espresso.IdlingResource
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import net.twisterrob.android.content.glide.MultiRequestListener
import net.twisterrob.android.content.glide.logging.LoggingListener
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * @see GlideIdlingResource
 */
class GlideIdlingResourceTest {

	@get:Rule val server = MockWebServer()
	@get:Rule val resetter = GlideResetRule()
	@get:Rule val testName = TestName()

	private fun sut(): IdlingResource =
		GlideIdlingResource(strict = true)

	@Test(timeout = 5_000)
	fun testIdleWhenNothingIsHappening() {
		val resource: IdlingResource = sut()
		val called = AtomicInteger()
		resource.registerIdleTransitionCallback {
			called.incrementAndGet()
		}
		assertEquals(0, called.get())
		assertTrue(resource.isIdleNow)
		assertEquals(1, called.get())
		assertTrue(resource.isIdleNow)
		assertEquals(2, called.get())
		assertTrue(resource.isIdleNow)
		assertEquals(3, called.get())
	}

	@Test(timeout = 30_000)
	fun testNotIdleWhenGlideIsLoading() {
		val resource: IdlingResource = sut()
		launchActivity<TestGlideActivity>().use { scenario ->
			/** Count how many times the idle callback was called. */
			val callbacks = AtomicInteger()

			/** Block test execution until the callback is called. */
			val callback = CountDownLatch(1)

			/** Flag to let the idle callback know that the test execution should be unblocked. */
			val unblockCallback = AtomicBoolean(false)
			// Simulate IdlingRegistry registering the resource:
			// IdlingResourceRegistry.IdlingState.registerSelf calls register + isIdleNow.
			resource.registerIdleTransitionCallback {
				callbacks.incrementAndGet()
				// Cannot check this here, because it becomes recursive. Checking after await.
				//assertTrue(resource.isIdleNow)
				// Unblock test for completion, if it asked (helps to ignore some callbacks).
				if (unblockCallback.get()) {
					callback.countDown()
				}
			}
			// Glide didn't make a request yet, so it's idle. Required to register the executors.
			assertTrue(resource.isIdleNow)
			// By contract isIdleNow == true calls the callback too.
			assertEquals(1, callbacks.get())
			val path = testName.methodName + ".png"
			val url = server.url(path)
			scenario.onActivity { activity ->
				Glide
					.with(activity)
					.load(url.toString())
					.timeout(Integer.MAX_VALUE)
					.listener(LoggingListener("Test ${path}"))
					.into(activity.imageView)
			}
			// This will block Glide from completing the load, because there's no response yet.
			assertNotNull("No request made", server.takeRequest(10, TimeUnit.SECONDS))
			// Sanity check that the callback is not called yet.
			assertEquals(1, callbacks.get())
			// This isIdleNow should attach whatever listeners needed into Glide.
			scenario.onActivity { assertFalse(resource.isIdleNow) }
			// Sanity check that the callback is not called yet.
			assertEquals(1, callbacks.get())
			// Sanity check that the callback is not unblocked yet.
			assertEquals(1, callback.count)
			// Tell the idling resource to unblock on next idle callback.
			unblockCallback.set(true)
			// Unblock Glide by giving it an image.
			server.enqueue(createColorResponse(Color.GREEN))
			// Block until Glide finishes, ResourceCallback.onTransitionToIdle should be called.
			assertTrue("Timed out", callback.await(10, TimeUnit.SECONDS))
			// Sanity check that the callback call was registered when it got unblocked.
			assertEquals(2, callbacks.get())
			// Glide finished, should be idle now.
			scenario.onActivity { assertTrue(resource.isIdleNow) }
			// By contract isIdleNow == true calls the callback too.
			assertEquals(3, callbacks.get())
		}
	}

	@Test(timeout = 30_000)
	fun testNotIdleWhenGlideFailing() {
		val resource: IdlingResource = sut()
		launchActivity<TestGlideActivity>().use { scenario ->
			/** Count how many times the idle callback was called. */
			val callbacks = AtomicInteger()

			/** Block test execution until the callback is called. */
			val callback = CountDownLatch(1)

			/** Flag to let the idle callback know that the test execution should be unblocked. */
			val unblockCallback = AtomicBoolean(false)
			// Simulate IdlingRegistry registering the resource:
			// IdlingResourceRegistry.IdlingState.registerSelf calls register + isIdleNow.
			resource.registerIdleTransitionCallback {
				callbacks.incrementAndGet()
				// Cannot check this here, because it becomes recursive. Checking after await.
				//assertTrue(resource.isIdleNow)
				// Unblock test for completion, if it asked (helps to ignore some callbacks).
				if (unblockCallback.get()) {
					callback.countDown()
				}
			}
			// Glide didn't make a request yet, so it's idle. Required to register the executors.
			assertTrue(resource.isIdleNow)
			// By contract isIdleNow == true calls the callback too.
			assertEquals(1, callbacks.get())
			val listener = CountDownLatch(1)
			scenario.onActivity { activity ->
				Glide
					.with(activity)
					.load(Resources.ID_NULL)
					.listener(
						MultiRequestListener(
							LoggingListener("invalid model in " + testName.methodName),
							object : RequestListener<Drawable> {
								override fun onResourceReady(
									resource: Drawable,
									model: Any,
									target: Target<Drawable>?,
									dataSource: DataSource,
									isFirstResource: Boolean
								): Boolean =
									error("Should never happen, because model is not valid.")

								override fun onLoadFailed(
									e: GlideException?,
									model: Any?,
									target: Target<Drawable>,
									isFirstResource: Boolean
								): Boolean {
									// Block Glide from completing until we tell it to finish.
									assertTrue(listener.await(10, TimeUnit.SECONDS))
									return false
								}
							})
					)
					.into(activity.imageView)
			}
			// Sanity check that the callback is not called yet.
			assertEquals(1, callbacks.get())
			// This isIdleNow should attach whatever listeners needed into Glide.
			scenario.onActivity { assertFalse(resource.isIdleNow) }
			// Sanity check that the callback is not called yet.
			assertEquals(1, callbacks.get())
			// Sanity check that the callback is not unblocked yet.
			assertEquals(1, callback.count)
			// Tell the idling resource to unblock on next idle callback.
			unblockCallback.set(true)
			// Unblock Glide by letting it finish the job calling the listener.
			listener.countDown()
			// Block until ResourceCallback.onTransitionToIdle is called, this might've already happened.
			assertTrue("Timed out", callback.await(10, TimeUnit.SECONDS))
			// Sanity check that the callback call was registered when it got unblocked.
			assertEquals(2, callbacks.get())
			// Glide finished, should be idle now.
			scenario.onActivity { assertTrue(resource.isIdleNow) }
			// By contract isIdleNow == true calls the callback too.
			assertEquals(3, callbacks.get())
		}
	}
}
