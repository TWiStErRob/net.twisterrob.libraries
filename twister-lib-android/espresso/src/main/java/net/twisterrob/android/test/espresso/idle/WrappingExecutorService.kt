package net.twisterrob.android.test.espresso.idle

import java.lang.reflect.InvocationTargetException
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * An [ExecutorService] with a template method that allows subclasses to [wrap][wrapTask] tasks
 * before they are executed in the [delegate].
 *
 * Some task wrapping might happen without execution.
 * For example, [invokeAny] wraps all tasks, and [execute] doesn't guarantee execution.
 */
abstract class WrappingExecutorService protected constructor(
	protected val delegate: ExecutorService,
) : ExecutorService by delegate {

	/**
	 * Wraps a [Callable] that will be executed on the [delegate].
	 * Wrapping will happen eagerly, while execution of [task] will be delayed by [delegate].
	 */
	protected abstract fun <T> wrapTask(task: Callable<T>): Callable<T>

	/**
	 * Wraps a [Runnable] that will be executed on the [delegate].
	 * Wrapping will happen eagerly, while execution of [task] will be delayed by [delegate].
	 */
	protected open fun wrapTask(task: Runnable): Runnable =
		wrapTask(task.asCallable()).asRunnable()

	override fun execute(command: Runnable) {
		delegate.execute(wrapTask(command))
	}

	override fun <T> submit(task: Callable<T>): Future<T> =
		delegate.submit(wrapTask(task))

	override fun submit(task: Runnable): Future<*> =
		delegate.submit(wrapTask(task))

	override fun <T> submit(task: Runnable, result: T): Future<T> =
		delegate.submit(wrapTask(task), result)

	@Throws(InterruptedException::class)
	override fun <T> invokeAll(tasks: Collection<Callable<T>>): List<Future<T>> =
		delegate.invokeAll(tasks.map(::wrapTask))

	@Throws(InterruptedException::class)
	override fun <T> invokeAll(tasks: Collection<Callable<T>>, timeout: Long, unit: TimeUnit)
		: List<Future<T>> =
		delegate.invokeAll(tasks.map(::wrapTask), timeout, unit)

	@Throws(InterruptedException::class, ExecutionException::class)
	override fun <T> invokeAny(tasks: Collection<Callable<T>>): T =
		delegate.invokeAny(tasks.map(::wrapTask))

	@Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
	override fun <T> invokeAny(tasks: Collection<Callable<T>>, timeout: Long, unit: TimeUnit)
		: T =
		delegate.invokeAny(tasks.map(::wrapTask), timeout, unit)

	override fun toString(): String =
		"${this::class.java}(${delegate})"

	companion object {
		@JvmStatic
		protected fun Runnable.asCallable(): Callable<Unit> =
			Callable {
				this.run()
			}

		@JvmStatic
		protected fun Callable<Unit>.asRunnable(): Runnable =
			Runnable {
				try {
					this.call()
				} catch (ex: InterruptedException) {
					Thread.currentThread().interrupt()
				} catch (e: Error) {
					throw e
				} catch (e: RuntimeException) {
					throw e
				} catch (e: Exception) {
					throw InvocationTargetException(e)
				}
			}
	}
}
