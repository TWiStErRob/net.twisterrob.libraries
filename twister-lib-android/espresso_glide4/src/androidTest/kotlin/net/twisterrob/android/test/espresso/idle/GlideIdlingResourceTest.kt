package net.twisterrob.android.test.espresso.idle

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import net.twisterrob.android.content.glide.MultiRequestListener
import net.twisterrob.android.content.glide.logging.LoggingListener
import net.twisterrob.android.test.espresso.ImageViewMatchers.withBitmap
import net.twisterrob.android.test.espresso.ImageViewMatchers.withColor
import net.twisterrob.android.test.espresso.ImageViewMatchers.withDrawable
import net.twisterrob.android.test.espresso.ImageViewMatchers.withPixelAt
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * @see GlideIdlingResource
 */
class GlideIdlingResourceTest {

	@get:Rule val server = MockWebServer()
	@get:Rule val resetter = GlideResetRule()
	@get:Rule val testName = TestName()

	private fun sut(): IdlingResource =
		GlideIdlingResource(true)

	@Test fun testIdleWhenNothingIsHappening() {
		val resource: IdlingResource = sut()
		assertTrue(resource.isIdleNow)
	}

	@Test(timeout = 30_000)
	fun testNotIdleWhenGlideIsLoading() {
		val resource: IdlingResource = sut()
		launchActivity<TestGlideActivity>().use { scenario ->
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
			val callback = CountDownLatch(1)
			// At this point Glide made the request, but didn't get a response yet.
			resource.registerIdleTransitionCallback {
				// Cannot check this here, because it becomes recursive. Checking after await.
				//assertTrue(resource.isIdleNow)
				// Unblock test for completion, checking the count to make sure only happens once.
				assertEquals(1, callback.count)
				callback.countDown()
			}
			// Sanity check that the callback is not called yet.
			assertEquals(1, callback.count)
			// This isIdleNow should attach whatever listeners needed into Glide.
			scenario.onActivity { assertFalse(resource.isIdleNow) }
			// Sanity check that the callback is not called yet.
			assertEquals(1, callback.count)
			// Unblock Glide by giving it an image.
			server.enqueue(createColorResponse(Color.GREEN))
			// Block until Glide finishes, ResourceCallback.onTransitionToIdle should be called.
			assertTrue("Timed out", callback.await(10, TimeUnit.SECONDS))
			// Clear listener, because it might be called again.
			resource.registerIdleTransitionCallback { }
			// Glide finished, should be idle now.
			scenario.onActivity { assertTrue(resource.isIdleNow) }
		}
	}

	@Test(timeout = 30_000)
	fun testNotIdleWhenGlideFailing() {
		val resource: IdlingResource = sut()
		launchActivity<TestGlideActivity>().use { scenario ->
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
			val callback = CountDownLatch(1)
			// At this point Glide tried to make a request and failed, it's in the callback waiting.
			resource.registerIdleTransitionCallback {
				// Cannot check this here, because it becomes recursive. Checking after await.
				//assertTrue(resource.isIdleNow)
				// Unblock test for completion, checking the count to make sure only happens once.
				assertEquals(1, callback.count)
				callback.countDown()
			}
			// Sanity check that the callback is not called yet.
			assertEquals(1, callback.count)
			// This isIdleNow should attach whatever listeners needed into Glide.
			scenario.onActivity { assertFalse(resource.isIdleNow) }
			// Sanity check that the callback is not called yet.
			assertEquals(1, callback.count)
			// Unblock Glide by letting it finish the job calling the listener.
			listener.countDown()
			// Block until ResourceCallback.onTransitionToIdle is called, this might've already happened.
			assertTrue("Timed out", callback.await(10, TimeUnit.SECONDS))
			// Clear listener, because it might be called again.
			resource.registerIdleTransitionCallback { }
			// Glide finished, should be idle now.
			scenario.onActivity { assertTrue(resource.isIdleNow) }
		}
	}

	@Test(timeout = 30_000)
	fun testRealNotIdleWhenGlideSuccessful() {
		launchActivity<TestGlideActivity>().use { scenario ->
			sut().whileRegistered {
				val path = testName.methodName + ".png"
				val url = server.url(path)
				scenario.onActivity { activity ->
					Glide
						.with(activity)
						.load(url.toString())
						.placeholder(android.R.color.black)
						.listener(LoggingListener("Test ${path}"))
						.into(activity.imageView)
				}
				// onActivity { } -> implicit Instrumentation.waitForIdleSync().
				scenario.onActivity { activity ->
					// We can't verify the placeholder with onView().check(), because Glide is not idle yet.
					assertThat(activity.imageView.drawable, withColor(equalTo(Color.BLACK)))
				}
				// Unblock Glide by giving it an image,
				server.enqueue(createColorResponse(Color.GREEN))
				// This should always pass because the idling resource is registered.
				onView(withId(TestGlideActivity.IMAGE_VIEW_ID))
					.check(matches(withBitmap(withPixelAt(0, 0, equalTo(Color.GREEN)))))
			}
		}
	}

	@Test(timeout = 30_000)
	fun testRealNotIdleWhenGlideFailing() {
		launchActivity<TestGlideActivity>().use { scenario ->
			sut().whileRegistered {
				scenario.onActivity { activity ->
					Glide
						.with(activity)
						.load(Resources.ID_NULL)
						.placeholder(android.R.color.white)
						.error(android.R.color.black)
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
										// Block Glide from completing for a few seconds.
										Thread.sleep(3_000)
										return false
									}
								})
						)
						.into(activity.imageView)
				}
				// This should always pass because the idling resource is registered
				// and waits until the listener/job is complete.
				onView(withId(TestGlideActivity.IMAGE_VIEW_ID))
					.check(matches(withDrawable(withColor(equalTo(Color.BLACK)))))
			}
		}
	}
}
