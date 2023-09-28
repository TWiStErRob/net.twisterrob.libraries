package net.twisterrob.android.content.glide.svg

import android.content.Context
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.request.target.Target
import com.caverock.androidsvg.SVG
import java.security.MessageDigest

class SvgAutoSizeTransformation : Transformation<SVG> {

	override fun transform(
		context: Context,
		resource: Resource<SVG>,
		outWidth: Int,
		outHeight: Int
	): Resource<SVG> {
		val svg = resource.get() // This should .clone(), but SVG does not support it, not even manually.
		val (width, height) = svg.calculateRenderSize(outWidth, outHeight)
		svg.documentWidth = width.toFloat()
		svg.documentHeight = height.toFloat()
		return SimpleResource(svg)
	}

	override fun updateDiskCacheKey(messageDigest: MessageDigest) {
		messageDigest.update("SvgAutoSizeTransformation".toByteArray(Key.CHARSET))
	}

	override fun equals(other: Any?): Boolean =
		other is SvgAutoSizeTransformation

	override fun hashCode(): Int =
		this::class.java.hashCode()
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
