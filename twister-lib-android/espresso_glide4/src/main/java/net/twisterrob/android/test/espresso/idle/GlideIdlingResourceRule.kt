package net.twisterrob.android.test.espresso.idle

import androidx.test.espresso.Espresso
import net.twisterrob.android.test.junit.IdlingResourceRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class GlideIdlingResourceRule : IdlingResourceRule(GlideIdlingResource(strict = true)) {
	override fun apply(base: Statement, description: Description): Statement {
		val statement = object : Statement() {
			// This will execute while the idling resource is registered.
			override fun evaluate() {
				Espresso.onIdle() // See GlideIdlingResource for more info why this is necessary.
				base.evaluate()
			}
		}
		return super.apply(statement, description)
	}
}
