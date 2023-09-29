package net.twisterrob.android.test.espresso.idle

import androidx.test.core.app.ApplicationProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.Engine
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger(GlideIdlingResource::class.java)

class GlideIdlingResource : AsyncIdlingResource() {
	private val callTransitionToIdle = Runnable { transitionToIdle() }
//	private var watcher: EngineIdleWatcher? = null
	private var currentEngine: Engine? = null

	override fun getName(): String = "Glide"

	override fun isIdle(): Boolean {
		// Glide is a singleton, hence Engine should be too; just lazily initialize when needed.
		// In case Glide is replaced, this will still work.
		val glide = Glide.get(ApplicationProvider.getApplicationContext())
		println(glide)
//STOPSHIP		val engine: Engine = GlideAccessor.getEngine(glide)
//		if (currentEngine !== engine) {
//			if (watcher != null) {
//				watcher.unsubscribe(callTransitionToIdle)
//			}
//			val oldWatcher = watcher
//			try {
//				watcher = EngineIdleWatcher(engine)
//				watcher.setLogEvents(isVerbose)
//			} finally {
//				if (currentEngine != null) {
//					LOG.warn(
//						"Engine changed from {}({}) to {}({})",
//						currentEngine, oldWatcher, engine, watcher
//					)
//				}
//				currentEngine = engine
//			}
//		}
		return isIdleCore()
	}

	private fun isIdleCore(): Boolean =
		true
//STOPSHIP		watcher.isIdle()

	override fun waitForIdleAsync() {
//STOPSHIP		watcher.subscribe(callTransitionToIdle)
	}

	override fun transitionToIdle() {
//STOPSHIP		watcher.unsubscribe(callTransitionToIdle)
		super.transitionToIdle()
	}
}
