package net.twisterrob.android.test.espresso.idle

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
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
class CountDownRequestListener<R : Any>(
	private val latch: CountDownLatch,
) : RequestListener<R> {

	override fun onResourceReady(
		resource: R,
		model: Any,
		target: Target<R>?,
		dataSource: DataSource,
		isFirstResource: Boolean
	): Boolean {
		latch.countDown()
		return false
	}

	override fun onLoadFailed(
		e: GlideException?,
		model: Any?,
		target: Target<R>,
		isFirstResource: Boolean
	): Boolean {
		latch.countDown()
		return false
	}
}
