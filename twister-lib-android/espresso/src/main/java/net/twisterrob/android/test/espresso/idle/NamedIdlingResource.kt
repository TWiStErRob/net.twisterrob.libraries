package net.twisterrob.android.test.espresso.idle

import androidx.test.espresso.IdlingResource

fun IdlingResource.named(nameOverride: String? = null): IdlingResource =
	NamedIdlingResource(this, nameOverride)

/**
 * Some [IdlingResource]s does not have a [toString], which results in log lines like:
 * ```
 * Registering idling resources: [
 *     androidx.test.espresso.idling.CountingIdlingResource@3f2a6428,
 *     androidx.test.espresso.idling.CountingIdlingResource@2e810541,
 *     androidx.test.espresso.idling.CountingIdlingResource@2dcf3ae6
 * ]
 * ```
 * calling `CountingIdlingResource("counter").named()` or `SomeIdlingResource(...).named("override")`
 * and using those replacements to store, register/unregister will result in log lines like:
 * ```
 * Registering idling resources: [counter, override]
 * ```
 *
 * *WARNING*: Be careful not to register `.named()` and unregister the original.
 */
class NamedIdlingResource(
	idlingResource: IdlingResource,
	private val nameOverride: String? = null,
) : IdlingResource by idlingResource {

	override fun toString(): String =
		nameOverride ?: name
}
