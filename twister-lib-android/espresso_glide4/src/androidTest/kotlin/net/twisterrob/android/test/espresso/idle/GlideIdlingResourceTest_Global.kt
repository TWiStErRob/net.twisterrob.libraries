package net.twisterrob.android.test.espresso.idle

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.rules.RuleChain

/**
 * @see GlideIdlingResource
 */
class GlideIdlingResourceTest_Global : BaseGlideIdlingRuleTest(
	glide = RuleChain
		.emptyRuleChain()
		.around(GlideResetRule())
) {

	@Before fun setUp() {
		Espresso.onIdle() // See GlideIdlingResource for more info why this is necessary.
	}

	/**
	 * This is the gist of GlideIdlingResourceRule, replicated here for independent testing.
	 */
	companion object {
		private val resource = GlideIdlingResource(strict = false)

		@BeforeClass
		@JvmStatic
		fun setUpClass() {
			IdlingRegistry.getInstance().register(resource)
		}

		@AfterClass
		@JvmStatic
		fun tearDownClass() {
			IdlingRegistry.getInstance().unregister(resource)
		}
	}
}
