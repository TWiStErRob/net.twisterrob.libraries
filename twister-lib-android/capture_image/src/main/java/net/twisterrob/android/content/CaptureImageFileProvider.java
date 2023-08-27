package net.twisterrob.android.content;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.utils.tools.IOTools;

public class CaptureImageFileProvider extends FileProvider {

	/**
	 * Warning: this is used inlined in {@code image__capture_paths.xml} because @path doesn't support string resources
	 */
	private static final String PUBLIC_CAPTURE_IMAGE_FOLDER_NAME = "capture_image";

	@Override public void attachInfo(@NonNull Context context, @NonNull ProviderInfo info) {
		StrictMode.ThreadPolicy policy = StrictMode.allowThreadDiskReads();
		try {
			super.attachInfo(context, info);
		} finally {
			StrictMode.setThreadPolicy(policy);
		}
	}

	public static @NonNull Uri getUriForFile(@NonNull Context context, @NonNull File file) {
		ProviderInfo provider = AndroidTools.findProviderAuthority(context, CaptureImageFileProvider.class);
		return FileProvider.getUriForFile(context, provider.authority, file);
	}

	public static @NonNull File getTempFile(@NonNull Context context, @NonNull String path) {
		File temp = new File(context.getCacheDir(), PUBLIC_CAPTURE_IMAGE_FOLDER_NAME);
		try {
			IOTools.ensure(temp);
		} catch (IOException ex) {
			throw new IllegalStateException("Cannot create directory " + PUBLIC_CAPTURE_IMAGE_FOLDER_NAME + " in cache", ex);
		}
		return new File(temp, path);
	}
}
