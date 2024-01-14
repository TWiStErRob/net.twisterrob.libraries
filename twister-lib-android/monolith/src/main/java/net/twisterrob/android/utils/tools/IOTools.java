package net.twisterrob.android.utils.tools;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.OsConstants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public /*static*/ abstract class IOTools extends net.twisterrob.java.io.IOTools {
	private static final Logger LOG = LoggerFactory.getLogger(IOTools.class);

	protected IOTools() {
		// prevent instantiation
	}

	// TODO merge with Cineworld
	//public static String getEncoding(final org.apache.http.HttpEntity entity);

	public static @Nullable Bitmap getImage(@NonNull final URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		InputStream input = null;
		try {
			connection.connect();
			input = connection.getInputStream();

			return BitmapFactory.decodeStream(input);
		} finally {
			closeConnection(connection, input);
		}
	}

	public static @Nullable String getAssetAsString(@NonNull Context context, @NonNull String fileName) {
		InputStream stream = null;
		try {
			stream = context.getAssets().open(fileName, AssetManager.ACCESS_STREAMING);
			return readAll(stream);
		} catch (IOException ex) {
			LOG.warn("Cannot open {}", fileName, ex);
			return null;
		} finally {
			ignorantClose(stream);
		}
	}

	/**
	 * {@link ZipFile} doesn't implement {@link Closeable} before API 19 so we need a specialized method.
	 * @param closeMe more specific than {@link #ignorantClose(Closeable)} won't throw {@link IncompatibleClassChangeError}
	 * @see <a href="https://android.googlesource.com/platform/libcore/+/9902f3494c6d983879d8b9cfe6b1f771cfefe703%5E%21/#F7">Finish off AutoCloseable.</a>
	 */
	@TargetApi(VERSION_CODES.KITKAT)
	public static void ignorantClose(@SuppressWarnings("TypeMayBeWeakened") @Nullable ZipFile closeMe) {
		if (closeMe != null) {
			try {
				closeMe.close();
			} catch (IOException e) {
				LOG.warn("Cannot close " + closeMe, e);
			}
		}
	}

	/**
	 * {@link Cursor} doesn't implement {@link Closeable} before 4.1.1_r1 so we need a specialized method.
	 * @param closeMe more specific than {@link #ignorantClose(Closeable)} won't throw {@link IncompatibleClassChangeError}
	 * @see <a href="https://github.com/android/platform_frameworks_base/commit/03bd302aebbb77f4f95789a269c8a5463ac5a840">Don't close the database until all references released.</a>
	 */
	@TargetApi(VERSION_CODES.JELLY_BEAN)
	public static void ignorantClose(@SuppressWarnings("TypeMayBeWeakened") @Nullable Cursor closeMe) {
		if (closeMe != null) {
			closeMe.close(); // doesn't declare to throw IOException
		}
	}

	// CONSIDER adding more specializations for other (Auto)Closeables 

	/**
	 * {@link ParcelFileDescriptor} doesn't implement {@link Closeable} before 4.1.1_r1 so we need a specialized method.
	 * @param closeMe more specific than {@link #ignorantClose(Closeable)} won't throw {@link IncompatibleClassChangeError}
	 * @see <a href="https://github.com/bumptech/glide/issues/157">ParcelFileDescriptor image loading is broken pre 4.1.1_r1</a>
	 * @see <a href="https://github.com/android/platform_frameworks_base/commit/e861b423790e5bf2d5a55b096065c6ad0541d5bb">Add Closeable to ParcelFileDescriptor, and always close any incoming PFDs when dumping.</a>
	 */
	@TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
	public static void ignorantClose(@SuppressWarnings("TypeMayBeWeakened") @Nullable ParcelFileDescriptor closeMe) {
		if (closeMe != null) {
			try {
				closeMe.close();
			} catch (IOException e) {
				LOG.warn("Cannot close " + closeMe, e);
			}
		}
	}

	@TargetApi(VERSION_CODES.KITKAT)
	public static void closeWithError(@NonNull ParcelFileDescriptor pfd, @NonNull String message) throws IOException {
		if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
			pfd.close();
		} else {
			pfd.closeWithError(message);
		}
	}

	@TargetApi(VERSION_CODES.KITKAT)
	public static void ignorantCloseWithError(@NonNull ParcelFileDescriptor pfd, @NonNull String message) {
		try {
			closeWithError(pfd, message);
		} catch (IOException e) {
			LOG.warn("Cannot close " + pfd + " with error: " + message, e);
		}
	}

	@TargetApi(VERSION_CODES.LOLLIPOP)
	public static boolean isEPIPE(@Nullable Throwable ex) {
		if (ex == null) {
			return false;
		}
		int code = -1;
		if (ex instanceof IOException) {
			ex = ex.getCause();
		}
		if (VERSION_CODES.LOLLIPOP <= VERSION.SDK_INT && ex instanceof ErrnoException) {
			code = ((ErrnoException)ex).errno;
		} else if ("ErrnoException".equals(ex.getClass().getSimpleName())) {
			// before 21 it's libcore.io.ErrnoException
			try {
				Field errno = ex.getClass().getDeclaredField("errno");
				code = (Integer)errno.get(ex);
			} catch (Throwable ignore) {
				// don't bother, we're doing best effort
			}
		}
		int epipe = VERSION_CODES.LOLLIPOP <= VERSION.SDK_INT? OsConstants.EPIPE : 32 /* from errno.h */;
		return code == epipe || (ex.getMessage() != null && ex.getMessage().contains("EPIPE"));
	}
}
