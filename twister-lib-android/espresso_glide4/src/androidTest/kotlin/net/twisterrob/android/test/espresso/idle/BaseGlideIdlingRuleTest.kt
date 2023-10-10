package net.twisterrob.android.test.espresso.idle

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.rules.TestRule

abstract class BaseGlideIdlingRuleTest(
	@get:Rule val glide: TestRule
) {
	@get:Rule val testName = TestName()
	@get:Rule val server = MockWebServer()

	@Test(timeout = 5_000)
	fun testIdleWhenNothingIsHappening() {
		// This could time out if Glide idling resource is reporting busy.
		Espresso.onIdle()
	}

	@Test(timeout = 30_000)
	fun testNotIdleWhenGlideSuccessful() {
		launchActivity<TestGlideActivity>().use { scenario ->
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

	@Test(timeout = 30_000)
	fun testNotIdleWhenGlideFailing() {
		launchActivity<TestGlideActivity>().use { scenario ->
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
