package net.twisterrob.android.content.glide

import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Canvas
import android.graphics.Rect
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class PaddingTransformation(
	private val padding: Int,
) : BitmapTransformation() {

	override fun transform(
		pool: BitmapPool,
		toTransform: Bitmap,
		outWidth: Int,
		outHeight: Int,
	): Bitmap {
		val dst = Rect(0, 0, outWidth, outHeight).apply { inset(padding, padding) }
		val result = pool[outWidth, outHeight, ARGB_8888]
		Canvas(result).run { drawBitmap(toTransform, null, dst, null) }
		return result
	}

	override fun updateDiskCacheKey(messageDigest: MessageDigest) {
		messageDigest.update("PaddingTransformation${padding}".toByteArray(CHARSET))
	}

	override fun equals(other: Any?): Boolean =
		other is PaddingTransformation && this.padding == other.padding

	override fun hashCode(): Int =
		padding
}
