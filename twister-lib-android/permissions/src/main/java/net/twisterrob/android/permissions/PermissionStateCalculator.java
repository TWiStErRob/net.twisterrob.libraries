package net.twisterrob.android.permissions;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

class PermissionStateCalculator {

	private final @NonNull PermissionsInterrogator interrogator;

	public PermissionStateCalculator(@NonNull PermissionsInterrogator interrogator) {
		this.interrogator = interrogator;
	}

	@AnyThread
	public @NonNull PermissionState currentState(@NonNull String... permissions) {
		if (!interrogator.hasAllPermissions(permissions)) {
			return PermissionState.DENIED;
		}
		return PermissionState.GRANTED;
	}
}
