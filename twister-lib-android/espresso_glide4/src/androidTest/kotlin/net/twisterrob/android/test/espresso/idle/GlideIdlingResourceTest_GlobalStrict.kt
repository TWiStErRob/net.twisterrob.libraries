package net.twisterrob.android.test.espresso.idle

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import com.bumptech.glide.Glide
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.startsWith
import org.junit.AfterClass
import org.junit.Assert.assertThrows
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.OrderWith
import org.junit.runner.manipulation.Alphanumeric
import java.util.concurrent.ExecutionException

/**
 * [GlideIdlingResource] in strict mode should fail when the [Glide] instance changes.
 *
 * @see GlideIdlingResource
 */
@OrderWith(Alphanumeric::class)
class GlideIdlingResourceTest_GlobalStrict {

	@get:Rule val resetter = GlideResetRule()

	@Test fun test1() {
		Espresso.onIdle()
	}

	/**
	 * ```
	 * java.lang.RuntimeException: ...
	 * at androidx.test.espresso.Espresso.onIdle(Espresso.java:362)
	 * at androidx.test.espresso.Espresso.onIdle(Espresso.java:382)
	 * at net.twisterrob.android.test.espresso.idle.GlideIdlingResourceGlobalStrictTest.test2(GlideIdlingResourceGlobalStrictTest.kt:50)
	 * ... 35 trimmed
	 * Caused by: java.util.concurrent.ExecutionException: ...
	 * at java.util.concurrent.FutureTask.report(FutureTask.java:93)
	 * at java.util.concurrent.FutureTask.get(FutureTask.java:163)
	 * at androidx.test.espresso.Espresso.onIdle(Espresso.java:12)
	 * ... 38 more
	 * Caused by: java.lang.IllegalStateException: Glide changed from com.bumptech.glide.Glide@3c17b47d(CompositeIdlingResource(Glide executors)) to com.bumptech.glide.Glide@24c41f72(CompositeIdlingResource(Glide executors))
	 * at net.twisterrob.android.test.espresso.idle.GlideIdlingResource.isIdleNow(GlideIdlingResource.kt:43)
	 * ```
	 */
	@Test fun test2() {
		val ex = assertThrows(RuntimeException::class.java) {
			Espresso.onIdle()
		}
		val exEx = ex.cause
		assertThat(exEx, instanceOf(ExecutionException::class.java))
		val cause = exEx?.cause
		assertThat(cause, instanceOf(IllegalStateException::class.java))
		assertThat(cause!!.message, startsWith("Glide changed from com.bumptech.glide.Glide"))
	}

	companion object {
		private val resource = GlideIdlingResource(strict = true)

		@BeforeClass
		@JvmStatic
		fun setUp() {
			IdlingRegistry.getInstance().register(resource)
		}

		@AfterClass
		@JvmStatic
		fun tearDown() {
			IdlingRegistry.getInstance().unregister(resource)
		}
	}
}
