package net.twisterrob.android.utils.tools;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.exifinterface.media.ExifInterface;

import net.twisterrob.android.activity.CaptureImage;

public /*internal*/ final class CropTools {
	/**
	 * Intentionally alien LOG tag, so that one tag covers all Capture activity.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(CaptureImage.class);

	public static final int MAX_SIZE_NO_MAX = 0;

	private CropTools() {
		throw new InternalError("Do not instantiate a utility class.");
	}

	@WorkerThread
	public static File crop(File file, RectF sel, int maxSize, int quality, @NonNull Bitmap.CompressFormat format) throws
			IOException {
		if (file == null || sel.isEmpty()) {
			return null;
		}
		final int[] originalSize = ImageTools.getSize(file);
		LOG.trace("Original image size: {}x{}", originalSize[0], originalSize[1]);

		// keep a single Bitmap variable so the rest could be garbage collected
		final int orientation = ImageTools.getExifOrientation(file);
		final RectF rotatedSel = ImageTools.rotateUnitRect(sel, orientation);
		final Rect imageRect = ImageTools.percentToSize(rotatedSel, originalSize[0], originalSize[1]);

		final float leeway = 0.10f;
		// calculating a sample size should speed up loading and lessen the probability of OOMs.
		final int sampleSize = maxSize == MAX_SIZE_NO_MAX
				? 1 : calcSampleSize(maxSize, leeway, imageRect.width(), imageRect.height());
		LOG.trace("Downsampling by {}x", sampleSize);

		Bitmap bitmap = ImageTools.crop(file, imageRect, sampleSize);
		LOG.info("Cropped {} = {} to size {}x{}", sel, imageRect, bitmap.getWidth(), bitmap.getHeight());
		if (maxSize != MAX_SIZE_NO_MAX) {
			bitmap = ImageTools.downscale(bitmap, maxSize, maxSize, leeway);
			LOG.info("Downscaled to size {}x{} by constraint {}±{}",
					bitmap.getWidth(), bitmap.getHeight(), maxSize, maxSize * leeway);
		}

		// @deprecated: experimental for now, don't enable; this would reduce OOMs even more,
		// because it would skip rotation which create a full copy of the bitmap
		// on the other hand, rotation should use less memory as saving (getPixels + YCC), so it may be unnecessary.
		@SuppressWarnings("ConstantConditions")
		final boolean exifRotate = Boolean.parseBoolean("false");
		ExifInterface exif = null;
		if (exifRotate) {
			exif = new ExifInterface(file.getAbsolutePath());
		} else {
			bitmap = ImageTools.rotateImage(bitmap, ImageTools.getExifRotation(orientation));
			LOG.info("Rotated to size {}x{} because {}({})",
					bitmap.getWidth(), bitmap.getHeight(), ImageTools.getExifString(orientation), orientation);
		}

		ImageTools.savePicture(bitmap, format, quality, true, file);
		if (exifRotate) {
			exif.saveAttributes(); // restore original Exif (most importantly the orientation)
		}

		LOG.info("Saved {}x{} {}@{} into {}", bitmap.getWidth(), bitmap.getHeight(), format, quality, file);
		return file;
	}

	@AnyThread
	private static int calcSampleSize(
			int maxSize,
			@SuppressWarnings("SameParameterValue")
			float leewayPercent,
			int sourceWidth,
			int sourceHeight
	) {
		// mirror calculations in ImageTools.downscale
		final float widthPercentage = maxSize / (float)sourceWidth;
		final float heightPercentage = maxSize / (float)sourceHeight;
		final float minPercentage = Math.min(widthPercentage, heightPercentage);

		final int targetWidth = Math.round(minPercentage * sourceWidth);
		final int targetHeight = Math.round(minPercentage * sourceHeight);
		LOG.trace("Downscale: {}x{} -> {}x{} ({}%) ± {}x{} ({}%)",
				sourceWidth, sourceHeight, targetWidth, targetHeight, minPercentage * 100,
				targetWidth * leewayPercent, targetHeight * leewayPercent, leewayPercent * 100);
		final int exactSampleSize = Math.min(sourceWidth / targetWidth, sourceHeight / targetHeight);
		int sampleSize = exactSampleSize <= 1? 1 : Integer.highestOneBit(exactSampleSize); // round down to 2^x
		LOG.trace("Chosen sample size based on size is {} rounded to {}", exactSampleSize, sampleSize);
		int longerSide = Math.max(sourceWidth, sourceHeight);
		int targetLongerSide = Math.max(targetWidth, targetHeight);
		if (Math.abs((float)longerSide / (sampleSize * 2) - targetLongerSide) < targetLongerSide * leewayPercent) {
			LOG.trace("The longer side {}px allows for leeway ({}%) of {}px when using sample size {}",
					longerSide, leewayPercent * 100, targetLongerSide * leewayPercent, sampleSize * 2);
			// this allows the loaded image size to be between [targetLongerSide * (1-leewayPercent), targetLongerSide]
			sampleSize = sampleSize * 2;
		}
		return sampleSize;
	}
}
