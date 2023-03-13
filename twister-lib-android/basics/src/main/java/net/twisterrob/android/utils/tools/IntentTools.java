package net.twisterrob.android.utils.tools;

import java.io.Serializable;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public /*static*/ abstract class IntentTools {

	protected IntentTools() {
		// static utility class
	}

	// TODEL https://issuetracker.google.com/issues/242048899#comment21 released in androidx-core:1.10.0
	/**
	 * Workaround for <a href=https://issuetracker.google.com/issues/240585930>
	 *     Intent.getParcelableExtra(String,Class) throws an NPE internally</a>.
	 *
	 * Similar to <a href=https://github.com/androidx/androidx/blob/a1997f091fe7b74f6f44188877f7703c4a60074a/core/core/src/main/java/androidx/core/content/IntentCompat.java#L233">AndroidX</a>
	 */
	@SuppressWarnings("deprecation")
	public static @Nullable <T extends Parcelable> T getParcelableExtra(
			@NonNull Intent intent, @Nullable String name, @NonNull Class<T> clazz) {
		// SIC Originally there was no <T : Parcelable>,
		// because https://issuetracker.google.com/issues/272973616,
		// but I've decided to add it anyway, because asking for non-parcelable super-type is strange.
		// This follows how getSerializableExtra works.
		if (Build.VERSION_CODES.TIRAMISU < Build.VERSION.SDK_INT) {
			// SIC Method was added in 33, but using strictly greater than 33.
			// See https://issuetracker.google.com/issues/240585930#comment6
			return intent.getParcelableExtra(name, clazz);
		} else {
			// SIC Delegate to the other one to closer match the behavior of the new method in API 33.
			Bundle mExtras = intent.getExtras();
			if (mExtras != null) {
				return BundleTools.getParcelable(mExtras, name, clazz);
			} else {
				return null;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static @Nullable <T extends Serializable> T getSerializableExtra(
			@NonNull Intent intent, @Nullable String name, @NonNull Class<T> clazz) {
		if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
			// Note: this is at least 33, because this method
			// is not affected by https://issuetracker.google.com/issues/240585930.
			return intent.getSerializableExtra(name, clazz);
		} else {
			// SIC Delegate to the other one to closer match the behavior of the new method in API 33.
			Bundle mExtras = intent.getExtras();
			if (mExtras != null) {
				return BundleTools.getSerializable(mExtras, name, clazz);
			} else {
				return null;
			}
		}
	}
}
