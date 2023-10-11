package net.twisterrob.android.test.espresso.idle

import androidx.test.espresso.idling.CountingIdlingResource
import java.util.concurrent.Executor

class CountingExecutor(
	private val countingIdlingResource: CountingIdlingResource,
	delegate: Executor,
) : WrappingExecutor(delegate) {

	override fun wrapCommand(command: Runnable): Runnable {
		countingIdlingResource.increment()
		return Runnable {
			try {
				command.run()
			} finally {
				countingIdlingResource.decrement()
			}
		}
	}

	override fun toString(): String =
		"${super.toString()} idling on '${countingIdlingResource.name}'"
}
