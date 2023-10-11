package net.twisterrob.android.test.espresso.idle

import androidx.test.espresso.idling.CountingIdlingResource
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit

class CountingExecutorService(
	private val countingIdlingResource: CountingIdlingResource,
	delegate: ExecutorService,
) : WrappingExecutorService(delegate) {

	override fun <T> wrapTask(task: Callable<T>): Callable<T> {
		countingIdlingResource.increment()
		val callable = Callable {
			try {
				task.call()
			} finally {
				countingIdlingResource.decrement()
			}
		}
		return when (task) {
			is Comparable<*> -> ComparableTask(task as Comparable<*>, callable)
			else -> callable
		}
	}

	override fun wrapTask(task: Runnable): Runnable {
		countingIdlingResource.increment()
		val runnable = Runnable {
			try {
				task.run()
			} finally {
				countingIdlingResource.decrement()
			}
		}
		return when (task) {
			is Comparable<*> -> ComparableTask(task as Comparable<*>, runnable.asCallable())
			else -> runnable
		}
	}

	override fun toString(): String =
		"${super.toString()} idling on '${countingIdlingResource.name}'"

	override fun <T> invokeAny(tasks: Collection<Callable<T>>): T =
		// wrapTask would increment many times before the first task is executed, but not all might be decremented.
		throw UnsupportedOperationException("Not implemented yet.")

	override fun <T> invokeAny(tasks: Collection<Callable<T>>, timeout: Long, unit: TimeUnit): T =
		// wrapTask would increment many times before the first task is executed, but not all might be decremented.
		throw UnsupportedOperationException("Not implemented yet.")
}

/**
 * For compatibility with [PriorityBlockingQueue] which requires [Comparable] tasks.
 */
private class ComparableTask<C : Any, R : Any?>(
	private val comparable: Comparable<C>,
	private val task: Callable<R>,
) : Callable<R>, Comparable<C>, Runnable {

	override fun run() {
		task.call()
	}

	override fun call(): R =
		task.call()

	override fun compareTo(other: C): Int =
		if (other is ComparableTask<*, *>) {
			@Suppress("UNCHECKED_CAST")
			comparable.compareTo(other.comparable as C)
		} else {
			error("Can't compare ${this::class.java} to ${other::class.java}")
		}
}
