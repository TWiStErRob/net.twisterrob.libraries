package net.twisterrob.android.utils.tools;

import java.io.Serializable;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public /*static*/ abstract class BundleTools {

	protected BundleTools() {
		// static utility class
	}

	/**
	 * Generic access to bundle, don't know the type.
	 * This is sometimes a valid case, in that case, this method hides the deprecation warning.
	 */
	@SuppressWarnings("deprecation")
	public static @Nullable Object getObject(@NonNull Bundle bundle, @NonNull String key) {
		return bundle.get(key);
	}

	// TODEL https://issuetracker.google.com/issues/242048899#comment21 released in androidx-core:1.10.0
	/**
	 * Workaround for <a href=https://issuetracker.google.com/issues/240585930>
	 *     Intent.getParcelableExtra(String,Class) throws an NPE internally</a>.
	 * Similar to <a href=https://github.com/androidx/androidx/blob/a1997f091fe7b74f6f44188877f7703c4a60074a/core/core/src/main/java/androidx/core/os/BundleCompat.java#L69">AndroidX</a>
	 */
	@SuppressWarnings("deprecation")
	public static @Nullable <T extends Parcelable> T getParcelable(
			@NonNull Bundle bundle, @Nullable String key, @NonNull Class<T> clazz) {
		// SIC Originally there was no <T : Parcelable>,
		// because https://issuetracker.google.com/issues/272973616,
		// but I've decided to add it anyway, because asking for non-parcelable super-type is strange.
		// This follows how getSerializable works.
		if (Build.VERSION_CODES.TIRAMISU < Build.VERSION.SDK_INT) {
			// SIC Method was added in 33, but using strictly greater than 33.
			// See https://issuetracker.google.com/issues/240585930#comment6
			return bundle.getParcelable(key, clazz);
		} else {
			return tryCast(key, clazz, bundle.getParcelable(key));
		}
	}

	@SuppressWarnings("deprecation")
	public static @Nullable <T extends Serializable> T getSerializable(
			@NonNull Bundle bundle, String key, Class<T> clazz) {
		if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
			// Note: this is at least 33, because this method
			// is not affected by https://issuetracker.google.com/issues/240585930.
			return bundle.getSerializable(key, clazz);
		} else {
			return tryCast(key, clazz, bundle.getSerializable(key));
		}
	}

	private static @Nullable <T> T tryCast(
			@Nullable String key, @NonNull Class<T> clazz, @Nullable Object value) {
		// SIC Cast-catch to match the exact behavior of the new methods in API 33.
		try {
			return clazz.cast(value);
		} catch (ClassCastException e) {
			Log.w("Bundle", "Key " + key + " expected " + clazz.getCanonicalName() +
					" but value was of a different type. The default value <null> was returned.");
			Log.w("Bundle", "Attempt to cast generated internal exception:", e);
			return null;
		}
	}
}
