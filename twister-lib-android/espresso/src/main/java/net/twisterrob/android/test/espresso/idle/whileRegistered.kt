@file:JvmMultifileClass
@file:JvmName("IdlingResourceExtensions")

package net.twisterrob.android.test.espresso.idle

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource

@JvmName("whileRegisteredKotlin")
inline fun IdlingResource.whileRegistered(block: () -> Unit) {
	whileRegistered(this, block)
}

inline fun whileRegistered(resource: IdlingResource, block: () -> Unit) {
	try {
		IdlingRegistry.getInstance().register(resource)
		block()
	} finally {
		IdlingRegistry.getInstance().unregister(resource)
	}
}

inline fun whileRegistered(vararg resources: IdlingResource, block: () -> Unit) {
	try {
		IdlingRegistry.getInstance().register(*resources)
		block()
	} finally {
		IdlingRegistry.getInstance().unregister(*resources)
	}
}

inline fun whileRegistered(resources: Iterable<IdlingResource>, block: () -> Unit) {
	whileRegistered(*resources.toList().toTypedArray(), block = block)
}
