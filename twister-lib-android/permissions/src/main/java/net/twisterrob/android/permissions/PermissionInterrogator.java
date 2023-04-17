package net.twisterrob.android.permissions;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionInterrogator {

	private final @NonNull Activity activity;

	public PermissionInterrogator(@NonNull Activity activity) {
		this.activity = activity;
	}

	public boolean hasPermission(@NonNull String permission) {
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
