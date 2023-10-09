package net.twisterrob.android.test.espresso.idle

import androidx.test.espresso.idling.CountingIdlingResource
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

class CountingExecutorService(
	private val countingIdlingResource: CountingIdlingResource,
	executorService: ExecutorService,
) : WrappingExecutorService(executorService) {

	override fun <T> wrapTask(task: Callable<T>): Callable<T> {
		countingIdlingResource.increment()
		return Callable {
			try {
				task.call()
			} finally {
				countingIdlingResource.decrement()
			}
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
