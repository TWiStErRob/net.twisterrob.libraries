package net.twisterrob.android.test.espresso.idle

import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.engine
import com.bumptech.glide.load.engine.Engine
import com.bumptech.glide.load.engine.EngineIdleWatcher

class GlideIdlingResource : AsyncIdlingResource() {
	private val callTransitionToIdle = Runnable { transitionToIdle() }
	private var watcher: EngineIdleWatcher? = null
	private var currentEngine: Engine? = null

	override fun getName(): String = "Glide"

	override fun isIdle(): Boolean {
		// Glide is a singleton, hence Engine should be too; just lazily initialize when needed.
		// In case Glide is replaced, this will still work.
		val glide = Glide.get(ApplicationProvider.getApplicationContext())
		val engine = glide.engine
		if (currentEngine !== engine) {
			watcher?.unsubscribe(callTransitionToIdle)
			val oldWatcher = watcher
			try {
				watcher = EngineIdleWatcher(engine, isVerbose)
			} finally {
				if (currentEngine != null) {
					error("Engine changed from ${currentEngine}(${oldWatcher}) to ${engine}(${watcher})")
				}
				currentEngine = engine
			}
		}
		return isIdleCore()
	}

	private fun isIdleCore(): Boolean =
		watcher!!.isIdle

	override fun waitForIdleAsync() {
		watcher!!.subscribe(callTransitionToIdle)
	}

	override fun transitionToIdle() {
		watcher!!.unsubscribe(callTransitionToIdle)
		super.transitionToIdle()
	}
}
