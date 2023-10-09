package net.twisterrob.android.test.espresso.idle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.load.engine.directExecutor
import com.bumptech.glide.load.engine.mainThreadExecutor
import org.junit.rules.ExternalResource
import java.util.concurrent.Executor

open class GlideResetRule @JvmOverloads constructor(
	private val appContext: Context = ApplicationProvider.getApplicationContext()
) : ExternalResource() {

	private var directExecutorBackup: Executor? = null
	private var mainThreadExecutorBackup: Executor? = null

	override fun before() {
		GlideResetter.resetGlide(appContext)
		directExecutorBackup = directExecutor
		mainThreadExecutorBackup = mainThreadExecutor
	}

	override fun after() {
		GlideResetter.resetGlide(appContext)
		directExecutor = directExecutorBackup!!
		directExecutorBackup = null
		mainThreadExecutor = mainThreadExecutorBackup!!
		mainThreadExecutorBackup = null
	}
}
