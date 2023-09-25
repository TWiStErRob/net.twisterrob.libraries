@file:JvmName("GlideHelpers")

package net.twisterrob.android.content.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter

@JvmField
val NO_POOL: BitmapPool = BitmapPoolAdapter()

fun Context.getDefaultFormat(): DecodeFormat {
	return try {
		val getDecodeFormat = Glide::class.java.getDeclaredMethod("getDecodeFormat")
		val glide = Glide.get(this)
		getDecodeFormat.invoke(glide) as DecodeFormat
	} catch (e: Exception) {
		DecodeFormat.DEFAULT
	}
}
