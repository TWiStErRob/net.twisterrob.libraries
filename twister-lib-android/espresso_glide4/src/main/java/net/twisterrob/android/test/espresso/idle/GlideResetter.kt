package net.twisterrob.android.test.espresso.idle

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import net.twisterrob.android.test.junit.InstrumentationExtensions
import org.slf4j.LoggerFactory

/**
 * Destroys [Glide] state.
 * Any currently running or stored [Activity]s will need to be recreated to clear their [RequestManager]s.
 * This is normally not a problem because in instrumentation tests each test runs a separate Activity.
 */
object GlideResetter {
	private val LOG = LoggerFactory.getLogger(GlideResetter::class.java)

	/** Try to get rid of references, and clean and forget as much as possible. */
	fun resetGlide(context: Context) {
		if (!isInitialized()) {
			LOG.info("Not resetting Glide for {}, not initialized.", context)
			return
		}
		LOG.info("Resetting Glide for {}", context)
		cleanupGlide(context)
		forgetGlide()
	}

	@SuppressLint("VisibleForTests")
	private fun isInitialized(): Boolean =
		Glide.isInitialized()

	private fun cleanupGlide(context: Context) {
		InstrumentationExtensions.runOnMainIfNecessary {
			Glide.with(context).onDestroy()
			Glide.get(context).clearMemory()
		}
		Glide.get(context).clearDiskCache()
	}

	@SuppressLint("VisibleForTests")
	private fun forgetGlide() {
		// make sure Glide.get(...) never returns the old one
		Glide.tearDown()
	}
}
