package net.twisterrob.android.test.espresso.idle

import org.junit.rules.RuleChain

/**
 * @see GlideIdlingResourceRule
 */
class GlideIdlingResourceRuleTest : BaseGlideIdlingRuleTest(
	RuleChain
		.emptyRuleChain()
		.around(GlideResetRule())
		.around(GlideIdlingResourceRule())
)
