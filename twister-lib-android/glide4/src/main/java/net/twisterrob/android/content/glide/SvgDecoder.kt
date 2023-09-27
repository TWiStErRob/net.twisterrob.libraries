package net.twisterrob.android.content.glide

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.request.target.Target
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import java.io.IOException
import java.io.InputStream

/**
 * Decodes an SVG internal representation from an [InputStream].
 */
class SvgDecoder : ResourceDecoder<InputStream, SVG> {

	override fun handles(source: InputStream, options: Options): Boolean = true

	@Throws(IOException::class)
	override fun decode(
		source: InputStream,
		width: Int,
		height: Int,
		options: Options,
	): Resource<SVG> {
		try {
			val svg = SVG.getFromInputStream(source) ?: error("Cannot load SVG from stream")
			val (renderWidth, renderHeight) = svg.calculateRenderSize(width, height)
			svg.documentWidth = renderWidth.toFloat()
			svg.documentHeight = renderHeight.toFloat()
			return SimpleResource(svg)
		} catch (ex: SVGParseException) {
			throw IOException("Cannot load SVG from stream", ex)
		}
	}
}

private fun SVG.calculateRenderSize(width: Int, height: Int): Pair<Int, Int> {
	val actualWidth: Int
	val actualHeight: Int
	when {
		width == Target.SIZE_ORIGINAL && height == Target.SIZE_ORIGINAL -> {
			val docWidth = documentWidth.toInt()
			val docHeight = documentHeight.toInt()
			if (docWidth <= 0 || docHeight <= 0) {
				val viewBox = documentViewBox
				actualWidth = viewBox.width().toInt()
				actualHeight = viewBox.height().toInt()
			} else {
				actualWidth = docWidth
				actualHeight = docHeight
			}
		}

		width == Target.SIZE_ORIGINAL && height != Target.SIZE_ORIGINAL -> {
			actualWidth = (height * documentAspectRatio).toInt()
			actualHeight = height
		}

		height == Target.SIZE_ORIGINAL && @Suppress("KotlinConstantConditions") (width != Target.SIZE_ORIGINAL) -> {
			actualWidth = width
			actualHeight = (width / documentAspectRatio).toInt()
		}

		else -> {
			// Both width and height requested, SVG should scale into size.
			actualWidth = width
			actualHeight = height
		}
	}
	require(!(actualWidth <= 0 || actualHeight <= 0)) { "Either the Target or the SVG document must declare a size." }
	return Pair(actualWidth, actualHeight)
}
