package net.twisterrob.android.permissions;

import java.util.Map;

import androidx.annotation.NonNull;

public class PermissionsInterrogator {

	private final @NonNull PermissionInterrogator interrogator;

	public PermissionsInterrogator(@NonNull PermissionInterrogator interrogator) {
		this.interrogator = interrogator;
	}

	public boolean hasAllPermissions(@NonNull String... permissions) {
		for (String permission : permissions) {
			if (!interrogator.hasPermission(permission)) {
				return false;
			}
		}
		return true;
	}

	public boolean needsAnyRationale(@NonNull String... permissions) {
		for (String permission : permissions) {
			if (interrogator.needsRationale(permission)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAllGranted(@NonNull Map<String, Boolean> permissions) {
		for (Boolean isGranted : permissions.values()) {
			if (!interrogator.isGranted(isGranted)) {
				return false;
			}
		}
		return true;
	}
}
