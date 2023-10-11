package net.twisterrob.android.test.espresso.idle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.idleExecutors
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Recommended usage:
 * ```
 * @get:Rule val glide = RuleChain
 *                       .emptyRuleChain()
 *                       .around(GlideResetRule())
 *                       .around(GlideIdlingRule())
 * ```
 */
class GlideIdlingRule(
	private val context: Context = ApplicationProvider.getApplicationContext(),
	private val verbose: Boolean = false,
) : TestRule {

	override fun apply(base: Statement, description: Description): Statement =
		object : Statement() {
			override fun evaluate() {
				val glide = Glide.get(context)
				whileRegistered(idleExecutors(glide, verbose)) {
					base.evaluate()
				}
			}
		}
}
