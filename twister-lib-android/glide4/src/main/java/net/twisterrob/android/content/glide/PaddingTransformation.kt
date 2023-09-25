package net.twisterrob.android.content.glide;

import java.security.MessageDigest;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import androidx.annotation.NonNull;

public class PaddingTransformation extends BitmapTransformation {
	private final int padding;

	public PaddingTransformation(int padding) {
		this.padding = padding;
	}

	@Override protected Bitmap transform(@NonNull BitmapPool pool,
			@NonNull Bitmap toTransform, int outWidth, int outHeight) {
		Bitmap result = pool.get(outWidth, outHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		Rect dst = new Rect(0, 0, outWidth, outHeight);
		dst.inset(padding, padding);
		canvas.drawBitmap(toTransform, null, dst, null);
		return result;
	}

	@Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
		messageDigest.update((getClass().getSimpleName() + padding).getBytes(CHARSET));
	}
}
