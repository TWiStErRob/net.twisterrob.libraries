package net.twisterrob.android.content.glide.svg

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.caverock.androidsvg.SVG
import kotlin.math.roundToInt

/**
 * Assumes that the SVG has a document size set, see [SvgDecoder].
 */
class SvgBitmapTranscoder(
	private val bitmapPool: BitmapPool,
) : ResourceTranscoder<SVG, Bitmap> {

	override fun transcode(toTranscode: Resource<SVG>, options: Options): Resource<Bitmap> {
		val svg = toTranscode.get()
		val width = svg.documentWidth.roundToInt()
		val height = svg.documentHeight.roundToInt()
		require(0 < width && 0 < height) { "Invalid SVG document size." }

		val bitmap = bitmapPool[width, height, ARGB_8888]
		svg.renderToCanvas(Canvas(bitmap))
		return BitmapResource(bitmap, bitmapPool)
	}
}
