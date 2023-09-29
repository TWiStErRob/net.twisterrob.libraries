package net.twisterrob.android.test.espresso.idle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.ExternalResource

open class GlideResetRule @JvmOverloads constructor(
	private val appContext: Context = ApplicationProvider.getApplicationContext()
) : ExternalResource() {

	override fun before() {
		GlideResetter.resetGlide(appContext)
	}

	override fun after() {
		GlideResetter.resetGlide(appContext)
	}
}
