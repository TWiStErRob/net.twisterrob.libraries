package net.twisterrob.android.content.glide.logging

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import net.twisterrob.android.annotation.TrimMemoryLevel
import net.twisterrob.android.utils.tools.StringerTools
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger("glide.BitmapPool")

/**
 * ```
 * override fun applyOptions(context: Context, builder: GlideBuilder) {
 *     val calculator = MemorySizeCalculator.Builder(context).build()
 *     val originalPool = LruBitmapPool(calculator.bitmapPoolSize.toLong())
 *     builder.setBitmapPool(LoggingBitmapPool(originalPool))
 * }
 * ```
 * @see com.bumptech.glide.module.AppGlideModule.applyOptions
 */
class LoggingBitmapPool(
	private val wrapped: BitmapPool,
) : BitmapPool {

	override fun getMaxSize(): Long {
		val result = wrapped.maxSize
		LOG.trace("getMaxSize(): {}", result)
		return result
	}

	override fun setSizeMultiplier(sizeMultiplier: Float) {
		LOG.trace("setSizeMultiplier({})", sizeMultiplier)
		wrapped.setSizeMultiplier(sizeMultiplier)
	}

	override fun put(bitmap: Bitmap) {
		LOG.trace("put({})", StringerTools.toString(bitmap))
		wrapped.put(bitmap)
	}

	override fun get(width: Int, height: Int, config: Config): Bitmap {
		val result = wrapped[width, height, config]
		LOG.trace("get({}, {}, {}): {}", width, height, config, StringerTools.toString(result))
		return result
	}

	override fun getDirty(width: Int, height: Int, config: Config): Bitmap {
		val result = wrapped.getDirty(width, height, config)
		LOG.trace("getDirty({}, {}, {}): {}", width, height, config, StringerTools.toString(result))
		return result
	}

	override fun clearMemory() {
		LOG.trace("clearMemory()")
		wrapped.clearMemory()
	}

	override fun trimMemory(@TrimMemoryLevel level: Int) {
		LOG.trace("trimMemory({})", StringerTools.toTrimMemoryString(level))
		wrapped.trimMemory(level)
	}
}
