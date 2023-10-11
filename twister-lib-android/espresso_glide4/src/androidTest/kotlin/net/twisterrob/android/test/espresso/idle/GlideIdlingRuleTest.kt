package net.twisterrob.android.test.espresso.idle

import org.junit.rules.RuleChain

/**
 * @see GlideIdlingRule
 */
class GlideIdlingRuleTest : BaseGlideIdlingRuleTest(
	glide = RuleChain
		.emptyRuleChain()
		.around(GlideResetRule())
		.around(GlideIdlingRule())
)
