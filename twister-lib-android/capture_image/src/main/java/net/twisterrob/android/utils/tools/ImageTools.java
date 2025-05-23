package net.twisterrob.android.utils.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import net.twisterrob.java.io.IOTools;

// CONSIDER crop https://github.com/lorensiuswlt/AndroidImageCrop/blob/master/src/net/londatiga/android/MainActivity.java
// CONSIDER crop http://code.tutsplus.com/tutorials/capture-and-crop-an-image-with-the-device-camera--mobile-11458
@SuppressWarnings({"unused", "StaticMethodOnlyUsedInOneClass", "SameParameterValue"})
public /*static*/ abstract class ImageTools {
	private static final Logger LOG = LoggerFactory.getLogger(ImageTools.class);

	private static final String NO_SORT = null;
	private static final String NO_SELECTION = null;
	private static final String[] NO_ARGS = null;

	@SuppressWarnings("resource")
	public static Bitmap loadPicture(String sourceFile, int targetW, int targetH) throws IOException {
		Bitmap bitmap;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(sourceFile);
			bitmap = loadPicture(stream, targetW, targetH);
		} finally {
			IOTools.ignorantClose(stream);
		}
		if (bitmap != null) {
			int rotation = getExifRotation(new File(sourceFile));
			if (rotation != -1) {
				bitmap = rotateImage(bitmap, rotation);
			}
		}
		return bitmap;
	}
	public static Bitmap loadPicture(File sourceFile, int targetW, int targetH) throws IOException {
		return loadPicture(sourceFile.getAbsolutePath(), targetW, targetH);
	}
	public static Bitmap loadPicture(Context context, Uri sourceUri, int targetW, int targetH) throws IOException {
		Bitmap bitmap;
		InputStream stream = null;
		try {
			stream = context.getContentResolver().openInputStream(sourceUri);
			bitmap = loadPicture(stream, targetW, targetH);
		} finally {
			IOTools.ignorantClose(stream);
		}
		return bitmap;
	}
	public static Bitmap loadPicture(InputStream stream, int targetW, int targetH) {
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		if (targetW > 0 && targetH > 0) {
			bmOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(stream, null, bmOptions);

			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;
			bmOptions.inSampleSize = Math.min(photoW / targetW, photoH / targetH);
			bmOptions.inJustDecodeBounds = false;
		}

		return BitmapFactory.decodeStream(stream, null, bmOptions);
	}

	public static Bitmap loadPicture(Context context, int drawableResourceID) {
		return drawableToBitmap(ContextCompat.getDrawable(context, drawableResourceID));
	}
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable)drawable).getBitmap();
		}

		int width = drawable.getIntrinsicWidth();
		if (width <= 0) {
			width = 1;
		}
		int height = drawable.getIntrinsicHeight();
		if (height <= 0) {
			height = 1;
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static Bitmap rotateImage(Bitmap bitmap, int rotation) {
		Bitmap result = bitmap;
		if (rotation != 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(rotation);
			result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bitmap.recycle(); // Pretend none of this ever happened!
		}
		return result;
	}

	public static int getOrientation(Context context, Uri photoUri) {
		@SuppressWarnings("resource") Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] {MediaStore.Images.ImageColumns.ORIENTATION}, NO_SELECTION, NO_ARGS, NO_SORT);
		int result = ExifInterface.ORIENTATION_UNDEFINED;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					result = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION));
				}
			} finally {
				cursor.close();
			}
		}
		return result;
	}

	public static int getExifRotation(File file) throws IOException {
		return getExifRotation(getExifOrientation(file));
	}

	public static int getExifOrientation(File file) throws IOException {
		ExifInterface exif = new ExifInterface(file.getAbsolutePath());
		return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
	}

	public static int getExifRotation(int orientation) {
		switch (orientation) {
			case ExifInterface.ORIENTATION_NORMAL:
			case ExifInterface.ORIENTATION_UNDEFINED:
				return 0;
			case ExifInterface.ORIENTATION_ROTATE_90:
				return 90;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return 180;
			case ExifInterface.ORIENTATION_ROTATE_270:
				return 270;
			default:
				return -1;
		}
	}

	public static String getExifString(int orientation) {
		switch (orientation) {
			case ExifInterface.ORIENTATION_UNDEFINED:
				return "UNDEFINED";
			case ExifInterface.ORIENTATION_NORMAL:
				return "NORMAL";
			case ExifInterface.ORIENTATION_ROTATE_90:
				return "ROTATE_90";
			case ExifInterface.ORIENTATION_ROTATE_180:
				return "ROTATE_180";
			case ExifInterface.ORIENTATION_ROTATE_270:
				return "ROTATE_270";
			case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
				return "FLIP_HOR";
			case ExifInterface.ORIENTATION_FLIP_VERTICAL:
				return "FLIP_VER";
			case ExifInterface.ORIENTATION_TRANSPOSE:
				return "TRANSPOSE";
			case ExifInterface.ORIENTATION_TRANSVERSE:
				return "TRANSVERSE";
			default:
				return "unknown";
		}
	}

	/**
	 * Convert Uri into File, if possible.
	 *
	 * @return file A local file that the Uri was pointing to, or null if the
	 *         Uri is unsupported or pointed to a remote resource.
	 * @see #getPath(Context, Uri)
	 * @author paulburke
	 */
	public static File getFile(Context context, Uri uri) {
		if (uri != null) {
			String path = getPath(context, uri);
			if (path != null && isLocal(path)) {
				return new File(path);
			}
		}
		return null;
	}
	/**
	 * @return Whether the URI is a local one.
	 */
	public static boolean isLocal(String url) {
		return url != null && !url.startsWith("http://") && !url.startsWith("https://");
	}

	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.<br>
	 * <br>
	 * Callers should check whether the path is local before assuming it
	 * represents a local file.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @see #isLocal(String)
	 * @see #getFile(Context, Uri)
	 * @author paulburke
	 */
	public static String getPath(final Context context, final Uri uri) {
		LOG.trace("Authority: {}, Fragment: {}, Port: {}, Query: {}, Scheme: {}, Host: {}, Segments: {}",
				uri.getAuthority(), uri.getFragment(), uri.getPort(), uri.getQuery(), uri.getScheme(), uri.getHost(),
				uri.getPathSegments().toString()
		);
		String result = null;

		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			result = getPathKitKat(context, uri);
		}
		if ("content".equalsIgnoreCase(uri.getScheme())) { // MediaStore (and general)
			if ("com.google.android.apps.photos.content".equals(uri.getAuthority())) {
				result = uri.getLastPathSegment();
			} else {
				result = getDataColumn(context, uri, NO_SELECTION, NO_ARGS);
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) { // File
			result = uri.getPath();
		}

		return result;
	}

	@RequiresApi(VERSION_CODES.KITKAT)
	private static String getPathKitKat(final Context context, final Uri uri) {
		if (DocumentsContract.isDocumentUri(context, uri)) { // DocumentProvider
			if ("com.android.externalstorage.documents".equals(uri.getAuthority())) { // ExternalStorageProvider
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			} else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) { // DownloadsProvider
				String id = DocumentsContract.getDocumentId(uri);
				Uri base = Uri.parse("content://downloads/public_downloads");
				Uri contentUri = ContentUris.withAppendedId(base, Long.valueOf(id));
				return getDataColumn(context, contentUri, NO_SELECTION, NO_ARGS);
			} else if ("com.android.providers.media.documents".equals(uri.getAuthority())) { // MediaProvider
				String docId = DocumentsContract.getDocumentId(uri);
				String[] split = docId.split(":");
				String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				return getDataColumn(context, contentUri, "_id=?", split[1]);
			}
		}
		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 * @author paulburke
	 */
	public static String getDataColumn(Context context, Uri uri, String selection, String... selectionArgs) {
		final String column = "_data";
		Cursor cursor = null;
		try {
			//noinspection resource cursor is closed nicely
			cursor = context.getContentResolver().query(uri, new String[] {column}, selection, selectionArgs, NO_SORT);
			if (cursor != null && cursor.moveToFirst()) {
				//DatabaseTools.dumpCursor(cursor);
				return cursor.getString(cursor.getColumnIndexOrThrow(column));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public static Bitmap cropPicture(File file, int x, int y, int w, int h) throws IOException {
		return crop(file, new Rect(x, y, x + w, y + h), 1);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	public static RectF rotateUnitRect(RectF rect, int orientation) {
		RectF result = new RectF();
		switch (orientation) {
			default:
			case ExifInterface.ORIENTATION_UNDEFINED:
			case ExifInterface.ORIENTATION_NORMAL:
				// for consist copying, `return rect` would work too
				result.left = rect.left;
				result.top = rect.top;
				result.right = rect.right;
				result.bottom = rect.bottom;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				result.left = rect.top;
				result.top = 1 - rect.right;
				result.right = rect.bottom;
				result.bottom = 1 - rect.left;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				result.left = 1 - rect.right;
				result.top = 1 - rect.bottom;
				result.right = 1 - rect.left;
				result.bottom = 1 - rect.top;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				result.left = 1 - rect.bottom;
				result.top = rect.left;
				result.right = 1 - rect.top;
				result.bottom = rect.right;
				break;
		}
		return result;
	}

	public static Rect percentToSize(RectF unit, int width, int height) {
		return percentToSize(unit.left, unit.top, unit.right, unit.bottom, width, height);
	}

	@SuppressLint("Assert")
	private static Rect percentToSize(float left, float top, float right, float bottom, int width, int height) {
		assert 0 <= left && left <= 1 : "Left is not a percentage: " + left;
		assert 0 <= top && top <= 1 : "Top is not a percentage: " + top;
		assert 0 <= right && right <= 1 : "Right is not a percentage: " + right;
		assert 0 <= bottom && bottom <= 1 : "Bottom is not a percentage: " + bottom;
		Rect rect = new Rect();
		rect.left = (int)(left * width);
		rect.top = (int)(top * height);
		rect.right = (int)(right * width);
		rect.bottom = (int)(bottom * height);
		return rect;
	}

	/**
	 * @param rect in image space, no scaling applied
	 * @param sampleSize {@link BitmapFactory.Options#inSampleSize}, rect will be scaled as necessary
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("UseRequiresApi") // This method is ready for N usage and below accounting for idiosyncrasies.
	@TargetApi(VERSION_CODES.N)
	public static Bitmap crop(File file, Rect rect, int sampleSize) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		// The following is deprecated and ignored in N, but the below is the default value anyway.
		options.inDither = false;
		options.inSampleSize = sampleSize;
		// The following is deprecated and ignored in N, but the below is the default value anyway.
		options.inPreferQualityOverSpeed = true;
		return cropRegion(file, rect, options);
	}

	@RequiresApi(VERSION_CODES.GINGERBREAD_MR1)
	private static Bitmap cropRegion(File file, Rect rect, BitmapFactory.Options options) throws IOException {
		BitmapRegionDecoder decoder = null;
		try {
			decoder = newBitmapRegionDecoder(file.getAbsolutePath());
			if (decoder != null) {
				return decoder.decodeRegion(rect, options);
			}
		} finally {
			if (decoder != null) {
				decoder.recycle();
			}
		}
		return null;
	}

	/**
	 * @return according to API 33 contract, this is NonNull, but in API 21 it's documented as nullable.
	 *         Keep the nullability to opt for safety.
	 */
	@SuppressWarnings("deprecation")
	public static @Nullable BitmapRegionDecoder newBitmapRegionDecoder(@NonNull String pathName) throws IOException {
		if (VERSION_CODES.S <= VERSION.SDK_INT) {
			return BitmapRegionDecoder.newInstance(pathName);
		} else {
			return BitmapRegionDecoder.newInstance(pathName, true);
		}
	}

	private static BitmapFactory.Options getSizeInternal(File file) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		return options;
	}

	public static int[] getSize(File file) {
		BitmapFactory.Options options = getSizeInternal(file);
		return new int[] {options.outWidth, options.outHeight};
	}

	//	private static Bitmap cropPictureDecodeRegion(File file, int x, int y, int w, int h) throws IOException {
	//			int oWidth = options.outWidth;
	//			int oHeight = options.outHeight;
	//			int startingSize = 1;
	//			if (w * oWidth * h * oHeight > 1920 * 1080) {
	//				startingSize = w * oWidth * h * oHeight / (1920 * 1080) + 1;
	//			}
	//			options = new BitmapFactory.Options();
	//			options.inSampleSize = startingSize;

	//			for (options.inSampleSize = startingSize; options.inSampleSize <= 32; options.inSampleSize++) {
	//				try {
	//			return decoder.decodeRegion(new Rect((int)(l * oWidth), (int)(t * oHeight), (int)(r * oWidth),
	//					(int)(b * oHeight)), options);
	//				} catch (OutOfMemoryError e) {
	//					continue; // with for loop if OutOfMemoryError occurs
	//				}
	//			}
	//		return null;
	//	}

	public static void savePicture(Bitmap bitmap, CompressFormat format, int quality, boolean preferQualityOverSpeed,
			File file) throws IOException {
		savePicture(bitmap, format, quality, preferQualityOverSpeed, new FileOutputStream(file));
	}
	public static void savePicture(Bitmap bitmap, CompressFormat format, int quality, boolean preferQualityOverSpeed,
			OutputStream stream) throws IOException {
		try {
			if (preferQualityOverSpeed && format == CompressFormat.JPEG) {
				compressAsJPEG(bitmap, quality, stream);
			} else {
				bitmap.compress(format, quality, stream);
			}
			stream.flush();
		} finally {
			IOTools.ignorantClose(stream);
		}
	}

	public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, options);
	}

	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float)height / (float)reqHeight);
			} else {
				inSampleSize = Math.round((float)width / (float)reqWidth);
			}
		}

		return inSampleSize;
	}

	protected ImageTools() {
		// static utility class
	}

	/**
	 * Fits source into box defined by width/height, only one side will match.
	 *
	 * @param leewayPercent shortcut when source is between [targetSize, targetSize * (1+leeway)], use 0 for no leeway
	 * @see com.bumptech.glide.load.resource.bitmap.TransformationUtils#fitCenter
	 */
	public static Bitmap downscale(Bitmap source, int width, int height, float leewayPercent) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Non-positive sizes are not allowed.");
		}
		if (source.getWidth() == width && source.getHeight() == height) {
			LOG.trace("downscale: Source matches target: {}x{}, returning original", width, height);
			return source;
		}
		final float widthPercentage = width / (float)source.getWidth();
		final float heightPercentage = height / (float)source.getHeight();
		final float minPercentage = Math.min(widthPercentage, heightPercentage);

		final int targetWidth = Math.round(minPercentage * source.getWidth());
		final int targetHeight = Math.round(minPercentage * source.getHeight());

		if (source.getWidth() <= targetWidth && source.getHeight() <= targetHeight) {
			LOG.trace("downscale: Source ({}x{}) smaller than target ({}x{}), returning original",
					source.getWidth(), source.getHeight(), targetWidth, targetHeight);
			return source;
		}
		float leewayFactor = 1 + leewayPercent; // 100% + leeway
		if (source.getWidth() <= targetWidth * leewayFactor && source.getHeight() <= targetHeight * leewayFactor) {
			// this allows the image size to be between [targetSize, targetSize * (1+leeway)]
			// image sizes between [targetSize * (1-leeway), targetSize] will be handled by the previous if
			LOG.trace("downscale: Source ({}x{}) smaller than target ({}x{}) with {}% leeway, returning original",
					source.getWidth(), source.getHeight(),
					targetWidth * leewayFactor, targetHeight * leewayFactor,
					leewayPercent * 100);
			return source;
		}
		return scale(source, minPercentage);
	}

	public static Bitmap scale(Bitmap source, float scale) {
		final int targetWidth = Math.round(scale * source.getWidth());
		final int targetHeight = Math.round(scale * source.getHeight());
		LOG.trace("Downscaling {}({}x{}) by {}x -> {}x{}",
				source, source.getWidth(), source.getHeight(), scale, targetWidth, targetHeight);
		Bitmap.Config config = source.getConfig() != null? source.getConfig() : Bitmap.Config.ARGB_8888;
		Bitmap result = Bitmap.createBitmap(targetWidth, targetHeight, config);
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1) {
			result.setHasAlpha(source.hasAlpha());
		}

		Canvas canvas = new Canvas(result);
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Paint paint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(source, matrix, paint);

		return result;
	}

	/**
	 * Only even sized Bitmaps will be compressed 1:1, odd sized ones will have their last column/row removed.
	 * @see <a href="http://stackoverflow.com/q/36487971/253468">Compress JPEG with least quality loss on Android?</a>
	 */
	public static void compressAsJPEG(Bitmap bitmap, int quality, OutputStream stream) throws IOException {
		final int w = bitmap.getWidth();
		final int h = bitmap.getHeight();
		int[] argb = new int[w * h];
		bitmap.getPixels(argb, 0, w, 0, 0, w, h);
		byte[] ycc = LibJPEG.rgb_ycc_convert(argb, w, h);
		//noinspection UnusedAssignment let GC take it away, not used any more
		argb = null;
		// YuvImage doesn't handle odd-sized images, but the YCC conversion does.
		// Make sure to skip the extra CrCb data by setting the interleaved chroma stride to a value rounded up to even
		YuvImage yuvImage = new YuvImage(ycc, ImageFormat.NV21, w, h, new int[] {w, (w + 1) & ~1});
		if (!yuvImage.compressToJpeg(new Rect(0, 0, w, h), quality, stream)) {
			throw new IOException("JPEG compress failed for Bitmap of size " + w + "x" + h);
		}
	}

	/**
	 * Mathematically correct lossless RGB-YCC conversion
	 * based on {@code rgb_ycc_start} and {@code rgb_ycc_convert} from LibJPEG's {@code jcolor.c}.
	 *
	 * <p>Modifications and optimizations applied<br>
	 * time: mean execution time in ms ~ average absolute deviation)<br>
	 * all tests were compressing a similar image (picture of my wooden desk) 3 times in quick succession <br>
	 * </p>
	 * <ul>
	 * <li>original translation from C writing byte[] to File (529076 bytes): 300~30</li>
	 * <li>original translation from C writing to stream (72014 bytes) : 270~20</li>
	 * <li>move Cb and Cr calculation inside if (426009 bytes): 255~30</li>
	 * <li>clean up variable names, comments (541808 bytes): 230~3</li>
	 * <li>inlined table lookup, but FIX method call remains (571195 bytes): 500~30</li>
	 * <li>extracted constants initializing to FIX method call (654385 bytes): 230~10</li>
	 * <li>inlined FIX method to constant initialization (563229 bytes): 210~2</li>
	 * <li>%2==0 -> |&==0 (548441 bytes): 208~2</li>
	 * </ul>
	 *
	 * @see <a href="http://stackoverflow.com/q/36487971/253468">Compress JPEG with least quality loss on Android?</a>
	 */
	@SuppressWarnings({"PointlessArithmeticExpression", "PointlessBitwiseExpression"})
	private static class LibJPEG {
		/** Middle value in the [0, 256) range: {@value}. */
		private static final int CENTER_JSAMPLE = (Byte.MAX_VALUE - Byte.MIN_VALUE + 1) / 2;
		/** Speediest right-shift on some machines. */
		private static final int SCALE_BITS = 16;
		/** Scale factor to be applied to floating point numbers which are then rounded to allow integer arithmetic. */
		private static final int SCALE_FACTOR = 1 << SCALE_BITS;
		/** Constant to help rounding upwards (Y calculation), or almost upwards ({@link #FUDGE_FACTOR})*/
		private static final int ONE_HALF = SCALE_FACTOR / 2;
		/** tiny number in up-scaled range */
		private static final int EPSILON = 1;
		/**
		 * B => Cb, R => Cr multipliers are the same, we use a rounding fudge-factor for Cb and Cr.
		 * This ensures that the maximum output will round to 255 not 256, and thus that we don't have to range-limit.
		 */
		private static final int FUDGE_FACTOR = ONE_HALF - EPSILON;
		private static final int CHROMA_OFFSET = CENTER_JSAMPLE * SCALE_FACTOR + FUDGE_FACTOR;

		private static final int CYR = +(int)(0.5 + SCALE_FACTOR * 0.299);
		private static final int CYG = +(int)(0.5 + SCALE_FACTOR * 0.587);
		private static final int CYB = +(int)(0.5 + SCALE_FACTOR * 0.114);
		private static final int CbR = -(int)(0.5 + SCALE_FACTOR * 0.168735892);
		private static final int CbG = -(int)(0.5 + SCALE_FACTOR * 0.331264108);
		private static final int CbB = +(int)(0.5 + SCALE_FACTOR * 0.5);
		private static final int CrR = +(int)(0.5 + SCALE_FACTOR * 0.5);
		private static final int CrG = -(int)(0.5 + SCALE_FACTOR * 0.418687589);
		private static final int CrB = -(int)(0.5 + SCALE_FACTOR * 0.081312411);

		static byte[] rgb_ycc_convert(int[] argb, int width, int height) {
			// Handle odd-sized images based on http://stackoverflow.com/a/33821066/253468
			// [w * h * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8] would only work for even sizes
			// round width/height up to nearest even, e.g. 3x5 image would have 3*5 Y values and 4*6 chroma pairs
			final int chromaWidth = (width + 1) / 2;
			final int chromaHeight = (height + 1) / 2;
			final int frameSize = width * height;
			final byte[] ycc = new byte[frameSize + chromaWidth * chromaHeight * 2];

			int yIndex = 0; // Ys start at the beginning of the array, there are 1 for each pixel
			int uvIndex = frameSize; // CrCb pairs start after the Ys, there are 1 for each 4 pixels (or 1 or 2 if odd)
			int sourceIndex = 0;
			for (int y = 0; y < height; ++y) {
				for (int x = 0; x < width; ++x) {
					//int A = (argb[sourceIndex] >>> 24) & 0xff; // ignore alpha, no place for it in NV21/YUV420SP
					int R = (argb[sourceIndex] >>> 16) & 0xff;
					int G = (argb[sourceIndex] >>> 8) & 0xff;
					int B = (argb[sourceIndex] >>> 0) & 0xff;
					sourceIndex++;

					// If the inputs are [0, 256) (guaranteed by masking the component part we need),
					// the outputs of these equations must be too; we do not need an explicit range-limiting operation.
					// Hence the value being shifted is never negative, so it's safe to narrow-cast to byte.
					byte Y = (byte)((CYR * R + CYG * G + CYB * B + ONE_HALF) >>> SCALE_BITS);
					ycc[yIndex++] = Y;
					if (((x | y) & 1) == 0 /* === y % 2 == 0 && x % 2 == 0 */) { // top-left of a 2x2 sampling block
						byte Cb = (byte)((CbR * R + CbG * G + CbB * B + CHROMA_OFFSET) >>> SCALE_BITS);
						byte Cr = (byte)((CrR * R + CHROMA_OFFSET + CrG * G + CrB * B) >>> SCALE_BITS);
						ycc[uvIndex++] = Cr;
						ycc[uvIndex++] = Cb;
					}
				}
			}
			return ycc;
		}
	}
}
