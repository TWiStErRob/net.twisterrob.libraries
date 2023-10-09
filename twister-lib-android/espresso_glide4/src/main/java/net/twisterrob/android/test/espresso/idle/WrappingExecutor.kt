package net.twisterrob.android.test.espresso.idle

import java.util.concurrent.Executor

/**
 * An [Executor] with a template method that allows subclasses to [wrap][wrapCommand] commands
 * before they are executed on the [delegate].
 *
 * Some task wrapping might happen without execution as [execute] doesn't guarantee execution.
 */
abstract class WrappingExecutor(
	private val delegate: Executor,
) : Executor {

	/**
	 * Wraps a [Runnable] that will be executed on the [delegate].
	 * Wrapping will happen eagerly, while execution of [command] will be delayed by [delegate].
	 */
	abstract fun wrapCommand(command: Runnable): Runnable

	override fun execute(command: Runnable) {
		delegate.execute(wrapCommand(command))
	}

	override fun toString(): String =
		"${this::class.java}(${delegate})"
}
