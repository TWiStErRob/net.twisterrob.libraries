package net.twisterrob.android.test.espresso.idle

import androidx.annotation.GuardedBy
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import org.slf4j.LoggerFactory
import java.util.Collections
import java.util.IdentityHashMap

private val LOG = LoggerFactory.getLogger(CompositeIdlingResource::class.java)

/**
 * A composite [IdlingResource] that waits for all of its resources to be idle.
 *
 * This pretty much duplicates the logic in [IdlingRegistry], but it's useful to reduce the noise,
 * or otherwise combine multiple [IdlingResource]s as if they were one.
 *
 * @param resources WARNING: do not register these resources in the [IdlingRegistry]!
 */
class CompositeIdlingResource(
	private val name: String,
	private vararg val resources: IdlingResource,
	private val verbose: Boolean = false,
) : IdlingResource {

	init {
		resources.forEach { resource ->
			resource.registerIdleTransitionCallback { onTransitionToIdle(resource) }
		}
	}

	@GuardedBy("this")
	private val idled: MutableSet<IdlingResource> = Collections.newSetFromMap(IdentityHashMap())

	private lateinit var callback: ResourceCallback

	override fun getName(): String = name

	override fun registerIdleTransitionCallback(callback: ResourceCallback) {
		this.callback = callback
	}

	private fun onTransitionToIdle(resource: IdlingResource) {
		val isIdle = synchronized(this) {
			idled.add(resource)
			if (verbose) {
				LOG.debug("Resource: ${resource.name} transitioned to idle in ${name}, ${idled.size}/${resources.size} are idle.")
			}
			// isIdleCore is here only for performance short-circuit.
			// If the idled set is not full, we don't need to re-check everything.
			isIdleCore() && updateIdled()
		}
		if (isIdle) {
			if (verbose) {
				LOG.debug("All resources in ${this} have transitioned to idle.")
			}
			callback.onTransitionToIdle()
		}
	}

	override fun isIdleNow(): Boolean {
		val idleNow = updateIdled()
		if (idleNow) {
			callback.onTransitionToIdle()
		}
		return idleNow
	}

	private fun updateIdled(): Boolean =
		synchronized(this) {
			idled.clear()
			idled.addAll(resources.filter { it.isIdleNow })
			isIdleCore()
		}

	private fun isIdleCore(): Boolean =
		synchronized(this) {
			idled.size == resources.size
		}

	override fun toString(): String =
		"CompositeIdlingResource(${name})"
}
