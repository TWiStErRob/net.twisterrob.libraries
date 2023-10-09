package net.twisterrob.android.test.espresso.idle

import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.idleExecutors
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class GlideIdlingResourceRule : TestRule {
	override fun apply(base: Statement, description: Description): Statement =
		object : Statement() {
			override fun evaluate() {
				val glide = Glide.get(ApplicationProvider.getApplicationContext())
				whileRegistered(idleExecutors(glide)) {
					base.evaluate()
				}
			}
		}
}
