package net.twisterrob.android.test.espresso.idle

import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.util.Executors
import net.twisterrob.android.test.junit.InstrumentationExtensions
import org.slf4j.LoggerFactory

@SuppressLint("VisibleForTests")
object GlideResetter {
	private val LOG = LoggerFactory.getLogger(GlideResetter::class.java)

	/** Try to get rid of references and clean as much as possible  */
	fun resetGlide(context: Context) {
		if (!Glide.isInitialized()) {
			LOG.info("No need resetting Glide for {}", context)
			return
		}
		LOG.info("Resetting Glide for {}", context)
		cleanupGlide(context)
		forgetGlide()
	}

	private fun cleanupGlide(context: Context) {
		InstrumentationExtensions.runOnMainIfNecessary {
			Glide.with(context).onDestroy()
			Glide.get(context).clearMemory()
		}
		Glide.get(context).clearDiskCache()
	}

	private fun forgetGlide() {
		// make sure Glide.get(...) never returns the old one
		Glide.tearDown()
	}
}
