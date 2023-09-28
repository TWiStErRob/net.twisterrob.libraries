package net.twisterrob.android.content.glide.pooling

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.Downsampler
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder
import com.bumptech.glide.load.resource.transcode.BitmapDrawableTranscoder
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.bumptech.glide.load.resource.transcode.UnitTranscoder
import net.twisterrob.android.content.glide.append
import net.twisterrob.android.content.glide.register
import java.io.InputStream

/**
 * A [Bitmap] wrapper that is not tracked by [Glide]'s [BitmapPool], so it could save memory.
 * ```
 * Glide
 *     .with(…)
 *     .asDrawable() // or .asBitmap()
 *     .decode(NonPooledBitmap.class) // Required.
 *     .skipMemoryCache(true) // Recommended so the cleared load doesn't pressure the mem cache.
 *     .…
 *     .load(…) // Only InputStream-decodable models are supported for now.
 *     .into(…);
 * ```
 */
class NonPooledBitmap(
	internal val bitmap: Resource<Bitmap>,
) {
	companion object {
		@JvmStatic
		fun register(context: Context, glide: Glide, registry: Registry) {
			// Based on com.bumptech.glide.RegistryFactory.initializeDefaults()
			registry.append(
				InputStream::class,
				NonPooledBitmap::class,
				NonPooledBitmapResourceDecoder(
					StreamBitmapDecoder(
						Downsampler(
							registry.imageHeaderParsers,
							context.resources.displayMetrics,
							// Not glide.bitmapPool, so the resource returned is not tracked.
							BitmapPools.NO_POOL,
							glide.arrayPool
						),
						glide.arrayPool,
					)
				)
			)
			registry.register(
				NonPooledBitmap::class,
				BitmapDrawable::class,
				NonPooledBitmapTranscoder(BitmapDrawableTranscoder(context))
			)
			registry.register(
				NonPooledBitmap::class,
				Bitmap::class,
				NonPooledBitmapTranscoder(UnitTranscoder.get())
			)
		}
	}
}

private class NonPooledBitmapResourceDecoder(
	private val wrappedDecoder: ResourceDecoder<InputStream, Bitmap>,
) : ResourceDecoder<InputStream, NonPooledBitmap> {

	override fun handles(source: InputStream, options: Options): Boolean =
		wrappedDecoder.handles(source, options)

	override fun decode(source: InputStream, width: Int, height: Int, options: Options)
		: Resource<NonPooledBitmap>? {
		val bitmap = wrappedDecoder.decode(source, width, height, options)
		return bitmap?.let { NonPooledBitmapResource(NonPooledBitmap(it)) }
	}
}

private class NonPooledBitmapResource(
	private val data: NonPooledBitmap
) : Resource<NonPooledBitmap> {

	override fun getResourceClass(): Class<NonPooledBitmap> =
		NonPooledBitmap::class.java

	override fun get(): NonPooledBitmap =
		data

	override fun getSize(): Int =
		data.bitmap.size

	override fun recycle() {
		data.bitmap.recycle()
	}
}

private class NonPooledBitmapTranscoder<T>(
	private val wrappedTranscoder: ResourceTranscoder<Bitmap, T>,
) : ResourceTranscoder<NonPooledBitmap, T> {

	override fun transcode(toTranscode: Resource<NonPooledBitmap>, options: Options): Resource<T>? =
		wrappedTranscoder.transcode(toTranscode.get().bitmap, options)
}
