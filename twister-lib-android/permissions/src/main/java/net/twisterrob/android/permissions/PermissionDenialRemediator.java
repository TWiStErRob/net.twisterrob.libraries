package net.twisterrob.android.permissions;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.twisterrob.android.contracts.SettingsContracts;
import net.twisterrob.android.permissions.PermissionProtectedAction.PermissionEvents;
import net.twisterrob.android.utils.tools.DialogTools;
import net.twisterrob.android.utils.tools.PackageManagerTools;

class PermissionDenialRemediator {

	private static final Logger LOG = LoggerFactory.getLogger(PermissionDenialRemediator.class);

	@SuppressWarnings("ComparatorCombinators") // Comparator.comparing is API 24+.
	private static final Comparator<CharSequence> CS_AS_STRING_COMPARATOR =
			(o1, o2) -> String.valueOf(o1).compareTo(String.valueOf(o2));

	private final @NonNull ComponentActivity activity;
	private final @NonNull PermissionEvents.RationaleContinuation continuation;
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
		Set<CharSequence> groups = getGroups(activity.getPackageManager(), permissions);
		showRemediationDialog(groups);
	}

	private void showRemediationDialog(Set<CharSequence> groups) {
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

	private static @NonNull Set<CharSequence> getGroups(
			@NonNull PackageManager pm,
			@NonNull String[] permissions
	) {
		Set<CharSequence> groups = new TreeSet<>(CS_AS_STRING_COMPARATOR);
		for (String permission : permissions) {
			groups.add(inferPermissionGroupLabel(pm, permission));
		}
		return groups;
	}

	private static @Nullable CharSequence inferPermissionGroupLabel(
			@NonNull PackageManager pm,
			@NonNull String permission
	) {
		String group = getPermissionGroupOrNull(pm, permission);
		if (group == null || "android.permission-group.UNDEFINED".equals(group)) {
			try {
				PermissionInfo permissionInfo = pm.getPermissionInfo(permission, 0);
				return permissionInfo.loadLabel(pm);
			} catch (PackageManager.NameNotFoundException ex) {
				LOG.warn("Permission not found: {}", permission, ex);
				// Ignore, we'll return a fallback value.
			}
		} else {
			try {
				PermissionGroupInfo groupInfo = pm.getPermissionGroupInfo(group, 0);
				return groupInfo.loadLabel(pm);
			} catch (PackageManager.NameNotFoundException ex) {
				LOG.warn("Permission group not found: {}", group, ex);
				// Ignore, we'll return a fallback value.
			}
		}
		return permission.substring(permission.lastIndexOf('.') + 1);
	}

	private static @Nullable String getPermissionGroupOrNull(@NonNull PackageManager pm,
			@NonNull String permission) {
		try {
			PermissionInfo permissionInfo = pm.getPermissionInfo(permission, 0);
			return permissionInfo.group;
		} catch (PackageManager.NameNotFoundException ex) {
			LOG.warn("Permission not found: {}", permission, ex);
			return null;
		}
	}

	private static @NonNull String formatGroupList(@NonNull Set<CharSequence> groups) {
		StringBuilder sb = new StringBuilder();
		for (CharSequence group : groups) {
			sb.append("\n • ").append(group.toString());
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
