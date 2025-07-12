package net.twisterrob.orbit.logging

import kotlinx.coroutines.Job
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerDecorator
import org.orbitmvi.orbit.annotation.OrbitInternal
import org.orbitmvi.orbit.syntax.ContainerContext
import org.orbitmvi.orbit.syntax.IntentContext
import org.orbitmvi.orbit.syntax.Syntax

class LoggingContainerDecorator<STATE : Any, SIDE_EFFECT : Any>(
	override val actual: Container<STATE, SIDE_EFFECT>,
	private val events: OrbitEvents<STATE, SIDE_EFFECT>,
) : ContainerDecorator<STATE, SIDE_EFFECT> {

	interface OrbitEvents<STATE : Any, SIDE_EFFECT : Any> {

		fun intentStarted(transformer: suspend Syntax<STATE, SIDE_EFFECT>.() -> Unit)
		fun intentFinished(transformer: suspend Syntax<STATE, SIDE_EFFECT>.() -> Unit)

		fun sideEffect(sideEffect: SIDE_EFFECT)

		fun reduce(oldState: STATE, reducer: IntentContext<STATE>.() -> STATE, newState: STATE)
	}

	@OptIn(OrbitInternal::class)
	override fun orbit(orbitIntent: suspend ContainerContext<STATE, SIDE_EFFECT>.() -> Unit): Job =
		super.orbit {
			// Note need to do a double-capture resolution, because there's an intermediate internal function.
			// It was introduced in Orbit 6.0.0:
			// https://github.com/orbit-mvi/orbit-mvi/commit/14b9a9fa46fe62891498058065e5857a17a137f7#diff-be051a3776eb5c87c456768a7827d213e917534872f80832e4ab7020d59dc8bb
			events.intentStarted(orbitIntent.captured<Function<*>>("\$transformer").captured("\$transformer"))
			this.logged().orbitIntent()
			events.intentFinished(orbitIntent.captured<Function<*>>("\$transformer").captured("\$transformer"))
		}

	@OptIn(OrbitInternal::class)
	override suspend fun inlineOrbit(orbitIntent: suspend ContainerContext<STATE, SIDE_EFFECT>.() -> Unit) {
		super.inlineOrbit {
			events.intentStarted(orbitIntent.captured("\$transformer"))
			this.logged().orbitIntent()
			events.intentFinished(orbitIntent.captured("\$transformer"))
		}
	}

	/**
	 * @see org.orbitmvi.orbit.ContainerHost.intent
	 * @see org.orbitmvi.orbit.syntax.Syntax.reduce
	 */
	@OptIn(OrbitInternal::class)
	private fun ContainerContext<STATE, SIDE_EFFECT>.logged(): ContainerContext<STATE, SIDE_EFFECT> =
		ContainerContext(
			settings = settings,
			postSideEffect = {
				events.sideEffect(it)
				postSideEffect(it)
			},
			reduce = { reducer ->
				reduce { oldState ->
					reducer(oldState).also { newState ->
						val reducerName = if (isAndroid) "f$0" else "arg$1"
						events.reduce(oldState, reducer.captured(reducerName), newState)
					}
				}
			},
			subscribedCounter = subscribedCounter,
			stateFlow = stateFlow,
		)

	companion object {
		private val isAndroid: Boolean =
			try {
				android.os.Build.VERSION.SDK_INT != 0
			} catch (_: ClassNotFoundException) {
				false
			}
	}
}

/**
 * The framework's reducer and intent declarations are lambdas inside Orbit's functions.
 * We need to access the captured local variables to get our original reducer and intent lambdas.
 * These captured variables are kept in consumer ProGuard configuration.
 *
 * @see org.orbitmvi.orbit.ContainerHost.intent
 * @see org.orbitmvi.orbit.syntax.Syntax.reduce
 */
private fun <T : Function<*>> Function<*>.captured(localName: String): T =
	try {
		this::class.java.getDeclaredField(localName)
	} catch (ex: NoSuchFieldException) {
		val fields = this::class.java.declaredFields.joinToString { it.toGenericString() }
		throw NoSuchFieldException("Cannot find captured variable for $localName in ${this::class.java}: $fields")
			.initCause(ex)
	}
		.also { println(it.toGenericString()) }
		.apply { isAccessible = true }
		.get(this)
		.let { @Suppress("UNCHECKED_CAST") (it as T) }
