package net.twisterrob.android.test.espresso.idle

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import androidx.test.espresso.base.IdlingResourceRegistry
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.idleExecutors
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger(GlideIdlingResource::class.java)

/**
 * When registering this in [IdlingRegistry], make sure to call [Espresso.onIdle]
 * before the first [Glide.with] interaction.
 * If Glide is not hacked at the time of first interaction, idling might miss some background work.
 *
 * [Espresso.onIdle] forces a synchronization of [IdlingRegistry] into [IdlingResourceRegistry].
 * This makes sure that the [GlideIdlingResource] is eagerly hacking the current Glide instance.
 * Without eager initialization, it would be up to the test
 * to make sure there was something that idles Espresso BEFORE the first Glide load.
 *
 * @param strict does not allow changing Glide while the same idling resource is used.
 * In case of `true`, it's recommended to use [GlideIdlingRule] instead.
 */
class GlideIdlingResource(
	private val context: Context = ApplicationProvider.getApplicationContext(),
	private val strict: Boolean,
	private val verbose: Boolean = false,
) : IdlingResource {
	private lateinit var callback: ResourceCallback
	private var idlingResourceCallback: CancellableResourceCallback? = null
	private var idlingResource: IdlingResource? = null
	private var currentGlide: Glide? = null

	override fun getName(): String = "Glide"

	override fun registerIdleTransitionCallback(callback: ResourceCallback) {
		this.callback = callback
	}

	override fun isIdleNow(): Boolean {
		// Glide is a singleton, just lazily retrieve when needed.
		// In case Glide is replaced (e.g. via GlideResetter), this will still work (if not strict).
		val glide = Glide.get(context)
		if (currentGlide !== glide) {
			idlingResourceCallback?.cancel()
			val oldIdlingResource = idlingResource
			try {
				idlingResourceCallback = CancellableResourceCallback(callback)
				idlingResource = idleExecutors(glide, verbose).also {
					it.registerIdleTransitionCallback(idlingResourceCallback)
				}
			} finally {
				if (currentGlide != null) {
					val old = format(currentGlide, oldIdlingResource)
					val new = format(glide, idlingResource)
					val message = "Glide changed from ${old} to ${new}."
					if (strict) {
						error(message)
					} else {
						LOG.info(message)
					}
				}
				currentGlide = glide
			}
		}
		return isIdleCore()
	}

	private fun format(glide: Glide?, oldIdlingResource: IdlingResource?): String =
		"${glide ?: "No Glide"}(${oldIdlingResource ?: "No IdlingResource"})"

	private fun isIdleCore(): Boolean =
		idlingResource!!.isIdleNow
}

private class CancellableResourceCallback(
	private val resourceCallback: ResourceCallback
) : ResourceCallback {

	@Volatile
	private var cancelled: Boolean = false

	override fun onTransitionToIdle() {
		if (!cancelled) {
			resourceCallback.onTransitionToIdle()
		}
	}

	fun cancel() {
		cancelled = true
	}
}
