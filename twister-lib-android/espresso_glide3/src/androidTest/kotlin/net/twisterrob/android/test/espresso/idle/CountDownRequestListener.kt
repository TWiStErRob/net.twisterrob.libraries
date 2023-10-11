package net.twisterrob.android.test.espresso.idle

import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.concurrent.CountDownLatch

/**
 * Listener to do blocking Glide calls in Espresso tests without using [GlideIdlingResource].
 * ```
 * launchActivity<TestGlideActivity>().use { scenario ->
 *     val latch = CountDownLatch(1)
 *     scenario.onActivity { activity ->
 *         Glide
 *             .with(activity)
 *             .load(...)
 *             .listener(CountDownRequestListener(latch))
 *             .into(activity.imageView)
 *     }
 *     assertTrue("Timed out", latch.await(10, TimeUnit.SECONDS))
 *     // At this point we can be sure that the Glide load finished (success or failure).
 * }
 * ```
 */
class CountDownRequestListener<T, R>(
	private val latch: CountDownLatch,
) : RequestListener<T, R> {

	override fun onResourceReady(
		resource: R?,
		model: T?,
		target: Target<R>?,
		isFromMemoryCache: Boolean,
		isFirstResource: Boolean
	): Boolean {
		latch.countDown()
		return false
	}

	override fun onException(
		e: Exception?,
		model: T?,
		target: Target<R>?,
		isFirstResource: Boolean
	): Boolean {
		latch.countDown()
		return false
	}
}
