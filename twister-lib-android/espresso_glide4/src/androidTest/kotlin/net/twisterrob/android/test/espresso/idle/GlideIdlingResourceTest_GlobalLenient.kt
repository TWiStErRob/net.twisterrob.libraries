package net.twisterrob.android.test.espresso.idle

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import com.bumptech.glide.Glide
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.OrderWith
import org.junit.runner.manipulation.Alphanumeric

/**
 * [GlideIdlingResource] in lenient mode should allow changing [Glide] instances.
 *
 * @see GlideIdlingResource
 */
@OrderWith(Alphanumeric::class)
class GlideIdlingResourceTest_GlobalLenient {

	@get:Rule val resetter = GlideResetRule()

	@Test fun test1() {
		Espresso.onIdle()
	}

	@Test fun test2() {
		Espresso.onIdle()
	}

	companion object {
		private val resource = GlideIdlingResource(strict = false)

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
