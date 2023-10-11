package net.twisterrob.android.content.glide.pooling

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter

object BitmapPools {
	@JvmField
	val NO_POOL: BitmapPool = BitmapPoolAdapter()
}
