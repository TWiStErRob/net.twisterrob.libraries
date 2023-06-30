package net.twisterrob.android.permissions;

import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import net.twisterrob.android.contracts.SettingsContracts;
import net.twisterrob.android.permissions.PermissionProtectedAction.PermissionEvents;
import net.twisterrob.android.utils.tools.DialogTools;
import net.twisterrob.android.utils.tools.PackageManagerTools;

class PermissionDenialRemediator {

	private final @NonNull ComponentActivity activity;
	@NonNull private final PermissionEvents.RationaleContinuation continuation;
	private final @NonNull ActivityResultLauncher<String> settingsLauncher;

	public PermissionDenialRemediator(
			@NonNull ComponentActivity activity,
			@NonNull PermissionEvents.RationaleContinuation continuation
	) {
		this.activity = activity;
		this.continuation = continuation;
		this.settingsLauncher = activity.registerForActivityResult(
				new SettingsContracts.OpenApplicationDetails(),
				result -> continuation.rationaleAcceptedRetryRequest()
		);
	}

	public void remediatePermanentDenial(@NonNull String[] permissions) {
		Set<String> groups = getGroups(activity.getPackageManager(), permissions);
		showRemediationDialog(groups);
	}

	private void showRemediationDialog(Set<String> groups) {
		DialogTools
				.confirm(activity, value -> {
					if (Boolean.TRUE.equals(value)) {
						settingsLauncher.launch(activity.getPackageName());
					} else {
						continuation.rationaleRejectedCancelProcess();
					}
				})
				.setTitle(R.string.permissions_permanent_remediation_title)
				.setMessage(activity.getString(
						R.string.permissions_permanent_remediation_message,
						getAppName(activity),
						formatGroupList(groups)
				))
				.show()
		;
	}

	private static @NonNull Set<String> getGroups(
			@NonNull PackageManager pm,
			@NonNull String[] permissions
	) {
		Set<String> groups = new TreeSet<>();
		for (String permission : permissions) {
			try {
				PermissionInfo info = pm.getPermissionInfo(permission, 0);
				if ("android.permission-group.UNDEFINED".equals(info.group)) {
					groups.add(permission + " - " + info.loadLabel(pm).toString());
					continue;
				}
				PermissionGroupInfo groupInfo = pm.getPermissionGroupInfo(info.group, 0);
				groups.add(groupInfo.loadLabel(pm).toString());
			} catch (PackageManager.NameNotFoundException ex) {
				groups.add(permission);
			}
		}
		return groups;
	}

	private static @NonNull String formatGroupList(@NonNull Set<String> groups) {
		StringBuilder sb = new StringBuilder();
		for (String group : groups) {
			sb.append("\n • ").append(group);
		}
		return sb.toString();
	}

	private static @NonNull String getAppName(@NonNull Context context) {
		String appName;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = PackageManagerTools.getPackageInfo(pm, context.getPackageName(), 0);
			appName = info.applicationInfo.loadLabel(pm).toString();
		} catch (PackageManager.NameNotFoundException ex) {
			appName = context.getPackageName();
		}
		return appName;
	}
}
