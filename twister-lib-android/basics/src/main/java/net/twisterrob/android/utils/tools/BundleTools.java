package net.twisterrob.android.utils.tools;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public /*static*/ abstract class BundleTools {

	protected BundleTools() {
		// static utility class
	}

	@SuppressWarnings("deprecation") // Generic access to bundle, don't know the type.
	public static @Nullable Object getObject(@NonNull Bundle bundle, @NonNull String key) {
		return bundle.get(key);
	}
}
