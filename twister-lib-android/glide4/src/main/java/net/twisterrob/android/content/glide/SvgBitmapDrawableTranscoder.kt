package net.twisterrob.android.content.glide

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Picture
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.LazyBitmapDrawableResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.caverock.androidsvg.SVG

/**
 * Convert the [SVG]'s internal representation to an Android-compatible one ([Picture]).
 */
class SvgBitmapDrawableTranscoder(
	private val resources: Resources,
	private val bitmapTranscoder: ResourceTranscoder<SVG, Bitmap>,
) : ResourceTranscoder<SVG, BitmapDrawable> {

	override fun transcode(
		toTranscode: Resource<SVG>,
		options: Options,
	): Resource<BitmapDrawable>? {
		val bitmapResource = bitmapTranscoder.transcode(toTranscode, options)
		return LazyBitmapDrawableResource.obtain(resources, bitmapResource)
	}
}
