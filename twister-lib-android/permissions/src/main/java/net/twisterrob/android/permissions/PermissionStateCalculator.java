package net.twisterrob.android.permissions;

import android.app.Activity;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

class PermissionStateCalculator {

	private final PermissionsInterrogator interrogator;

	public PermissionStateCalculator(@NonNull Activity activity) {
		this.interrogator = new PermissionsInterrogator(activity);
	}
	@AnyThread
	public @NonNull PermissionState currentState(@NonNull String... permissions) {
		if (!interrogator.hasAllPermissions(permissions)) {
			return PermissionState.DENIED;
		}
		return PermissionState.GRANTED;
	}
}
