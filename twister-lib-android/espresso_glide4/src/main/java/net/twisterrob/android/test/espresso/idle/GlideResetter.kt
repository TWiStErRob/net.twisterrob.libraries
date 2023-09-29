package net.twisterrob.android.test.espresso.idle

import android.annotation.SuppressLint
import android.content.Context
import com.bumptech.glide.Glide
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
		restrictGlide(context)
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
	private fun restrictGlide(context: Context) {
		val glide = Glide.get(context)
		println(glide)
		//STOPSHIP
//		val engine = ReflectionTools.get<Engine>(glide, "engine")!!
//		ReflectionTools.set(engine, "jobs", object : HashMap<Any?, Any>() {
//
//			override fun put(key: Any?, value: Any): Any {
//				throw UnsupportedOperationException("This engine is dead.")
//			}
//
//			override fun remove(key: Any?): Any {
//				throw UnsupportedOperationException("This engine is dead.")
//			}
//		})
	}
}
