package net.twisterrob.android.test.espresso.idle

import android.content.Context
import android.graphics.Color
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import net.twisterrob.android.content.glide.LoggingListener
import net.twisterrob.android.content.glide.MultiRequestListener
import net.twisterrob.android.test.espresso.ImageViewMatchers.withBitmap
import net.twisterrob.android.test.espresso.ImageViewMatchers.withPixelAt
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * @see GlideResetter
 */
class GlideResetterTest {

	@get:Rule val server = MockWebServer()

	@Before fun setup() {
		// Sadly this has to work by itself, using production code to prove itself. :(
		// There's no other way to have separate Glides for each test.
		GlideResetter.resetGlide(ApplicationProvider.getApplicationContext())
	}

	@Test fun testResetChangesGlide() {
		val context: Context = ApplicationProvider.getApplicationContext()
		val first = Glide.get(context)

		val second = Glide.get(context)
		assertSame(first, second)

		GlideResetter.resetGlide(context)

		val third = Glide.get(context)
		assertNotSame(first, third)
	}

	@Test fun testResetChangesApplicationManager() {
		val context: Context = ApplicationProvider.getApplicationContext()
		val first = Glide.with(context)

		val second = Glide.with(context)
		assertSame(first, second)

		GlideResetter.resetGlide(context)

		val third = Glide.with(context)
		assertNotSame(first, third)
	}

	@Test fun testControlWithoutReset() {
		val color1 = Color.parseColor("#FFA0B0C0")
		launchActivity<TestGlideActivity>().use { scenario ->
			server.enqueue(createColorResponse(color1))
			scenario.load("image1.png")
			assertEquals(1, server.requestCount)
			checkRenderedImageView(color1)
		}
		// Loading a different image hits the server.
		launchActivity<TestGlideActivity>().use { scenario ->
			val color2 = Color.parseColor("#FFF0D0E0")
			server.enqueue(createColorResponse(color2))
			scenario.load("image2.png")
			assertEquals(2, server.requestCount)
			checkRenderedImageView(color2)
		}
		// Loading the same image again does not hit the server and shows same image.
		launchActivity<TestGlideActivity>().use { scenario ->
			val color3 = Color.parseColor("#FFA0C0E0")
			server.enqueue(createColorResponse(color3))
			scenario.load("image1.png")
			assertEquals(2, server.requestCount)
			checkRenderedImageView(color1)
		}
	}

	@Test fun testWithLoadDifferentWithReset() {
		launchActivity<TestGlideActivity>().use { scenario ->
			val color1 = Color.parseColor("#FFA0B0C0")
			server.enqueue(createColorResponse(color1))
			scenario.load("image1.png")
			assertEquals(1, server.requestCount)
			checkRenderedImageView(color1)
		}

		GlideResetter.resetGlide(ApplicationProvider.getApplicationContext())

		// Loading a different image hits the server.
		launchActivity<TestGlideActivity>().use { scenario ->
			val color2 = Color.parseColor("#FFF0D0E0")
			server.enqueue(createColorResponse(color2))
			scenario.load("image2.png")
			assertEquals(2, server.requestCount)
			checkRenderedImageView(color2)
		}
	}

	@Test fun testWithLoadSameWithReset() {
		launchActivity<TestGlideActivity>().use { scenario ->
			val color1 = Color.parseColor("#FFA0B0C0")
			server.enqueue(createColorResponse(color1))
			scenario.load("image1.png")
			assertEquals(1, server.requestCount)
			checkRenderedImageView(color1)
		}

		GlideResetter.resetGlide(ApplicationProvider.getApplicationContext())

		// Loading the same image again hits the server, because Glide caches are gone.
		launchActivity<TestGlideActivity>().use { scenario ->
			val color2 = Color.parseColor("#FFF0D0E0")
			server.enqueue(createColorResponse(color2))
			scenario.load("image1.png")
			assertEquals(2, server.requestCount)
			checkRenderedImageView(color2)
		}
	}

	@Test fun testWithLoadWithCachedAndResetManager() {
		launchActivity<TestGlideActivity>().use { scenario ->
			lateinit var cachedManager: RequestManager
			scenario.onActivity {
				cachedManager = Glide.with(it)
			}

			GlideResetter.resetGlide(ApplicationProvider.getApplicationContext())

			scenario.onActivity {
				val ex = assertThrows(UnsupportedOperationException::class.java) {
					cachedManager
						.load("not used")
						.into(it.imageView)
				}
				assertEquals("This engine is dead.", ex.message)
			}
			checkImageViewNotLoaded()
			assertEquals(0, server.requestCount)
		}
	}

	private fun checkImageViewNotLoaded() {
		onView(withId(TestGlideActivity.IMAGE_VIEW_ID))
			.check(matches(withBitmap(nullValue())))
	}

	private fun checkRenderedImageView(color: Int) {
		onView(withId(TestGlideActivity.IMAGE_VIEW_ID))
			.check(matches(withBitmap(withPixelAt(0, 0, equalTo(color)))))
	}

	private fun ActivityScenario<TestGlideActivity>.load(path: String) {
		val url = server.url(path)
		val latch = CountDownLatch(1)
		val asyncStack = Exception("Loading ${path} in onActivity { ... }")
		onActivity {
			try {
				Glide
					.with(it)
					.load(url.toString())
					.listener(
						MultiRequestListener(
							LoggingListener("Test ${path}"),
							CountDownRequestListener(latch),
						)
					)
					.into(it.imageView)
			} catch (ex: Throwable) {
				generateSequence(ex, Throwable::cause).last().initCause(asyncStack)
				throw ex
			}
		}
		assertTrue("Timed out", latch.await(10, TimeUnit.SECONDS))
	}
}
