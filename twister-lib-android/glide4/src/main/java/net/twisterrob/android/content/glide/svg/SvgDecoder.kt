package net.twisterrob.android.content.glide.svg

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import java.io.IOException
import java.io.InputStream

/**
 * Decodes an SVG internal representation from an [InputStream].
 */
class SvgDecoder(
	private val context: Context,
	private val transform: Transformation<SVG>? = null,
) : ResourceDecoder<InputStream, SVG> {

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
			return if (transform != null) {
				transform.transform(context, SimpleResource(svg), width, height)
			} else {
				SimpleResource(svg)
			}
		} catch (ex: SVGParseException) {
			throw IOException("Cannot load SVG from stream", ex)
		}
	}
}
