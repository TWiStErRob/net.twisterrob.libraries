package net.twisterrob.android.content.glide.svg

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.RawRes
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.request.target.Target
import com.caverock.androidsvg.SVGExternalFileResolver
import net.twisterrob.android.utils.tools.ResourceTools
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger(RawResourceSVGExternalFileResolver::class.java)

/**
 * TODO move registration to SvgDecoder in 1.5
 * @see [PR for TODO](https://github.com/BigBadaboom/androidsvg/pull/203)
 */
open class RawResourceSVGExternalFileResolver(
	private val context: Context,
	private val bitmapPool: BitmapPool,
) : SVGExternalFileResolver() {

	override fun resolveImage(filename: String): Bitmap? =
		runCatching { resolveImageViaGlideFlow(filename) }
			.onFailure { LOG.warn("Cannot resolve ${filename}", it) }
			.getOrNull()

	private fun resolveImageViaGlideFlow(filename: String): Bitmap {
		@RawRes val resId = resolveRawResourceId(filename)
		val resStream = context.resources.openRawResource(resId)
		val decoder = SvgDecoder(context, SvgAutoSizeTransformation())
		val transcoder = SvgBitmapTranscoder(bitmapPool)
		val options = Options()
		val svg = decoder.decode(resStream, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, options)
		return transcoder.transcode(svg, options).get()
	}

	@RawRes
	protected fun resolveRawResourceId(filename: String): Int =
		ResourceTools.getRawResourceID(context, filename.removeSuffix(".svg"))
}
