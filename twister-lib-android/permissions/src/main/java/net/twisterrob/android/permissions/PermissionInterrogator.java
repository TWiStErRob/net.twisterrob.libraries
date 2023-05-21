package net.twisterrob.android.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionInterrogator {

	private final @NonNull Activity activity;

	public PermissionInterrogator(@NonNull Activity activity) {
		this.activity = activity;
	}

	public boolean hasPermission(@NonNull String permission) {
		if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN
				&& Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
			// Doesn't exist on API 14, implicitly granted.
			return true;
		}
		int grant = ContextCompat.checkSelfPermission(activity, permission);
		return grant == PackageManager.PERMISSION_GRANTED;
	}

	public boolean needsRationale(@NonNull String permission) {
		return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
	}

	public boolean isGranted(Boolean grant) {
		return Boolean.TRUE.equals(grant);
	}
}
