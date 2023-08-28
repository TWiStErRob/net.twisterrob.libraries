package net.twisterrob.android.permissions;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Build;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;

import net.twisterrob.android.contracts.OpenApplicationDetailsInSettings;
import net.twisterrob.android.permissions.PermissionProtectedAction.PermissionEvents;
import net.twisterrob.android.utils.tools.DialogTools;
import net.twisterrob.android.utils.tools.PackageManagerTools;

class PermissionDenialRemediator {

	private static final Logger LOG = LoggerFactory.getLogger(PermissionDenialRemediator.class);

	@SuppressWarnings("ComparatorCombinators") // Comparator.comparing is API 24+.
	private static final Comparator<? super Object> AS_STRING_COMPARATOR =
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
				new OpenApplicationDetailsInSettings(),
				result -> continuation.rationaleAcceptedRetryRequest()
		);
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

	public void remediatePermanentDenial(@NonNull String[] permissions) {
		// Chaos spaghetti starts here, because API 31+ introduced async getGroupOfPlatformPermission method.
		// We need to call that deep in a for loop, then use the result on the UI thread to show a dialog.
		getGroupsAsync(
				activity.getPackageManager(),
				permissions,
				ContextCompat.getMainExecutor(activity),
				this::showRemediationDialog
		);
	}

	private static void getGroupsAsync(
			@NonNull PackageManager pm,
			@NonNull String[] permissions,
			@NonNull Executor executor,
			@NonNull Consumer<Set<CharSequence>> callback
	) {
		Set<CharSequence> groups = Collections.synchronizedSet(new TreeSet<>(AS_STRING_COMPARATOR));
		for (String permission : permissions) {
			queryGroupAsync(pm, permission, executor, group -> {
				groups.add(inferPermissionGroupLabel(pm, permission, group));
				if (groups.size() == permissions.length) {
					// We have no idea when the the callbacks will be called.
					// The executor doesn't receive a submit call until the data is ready,
					// so it's not possible to wait for all of them to be submitted
					// and simply rely on an executor to serialize the calls.
					// Moreover there are no guarantees the callbacks will execute in order.
					// The only way we know we're done is if received all callbacks.
					executor.execute(() -> callback.accept(groups));
				}
			});
		}
	}

	private static void queryGroupAsync(
			@NonNull PackageManager pm,
			@NonNull String permission,
			@NonNull Executor executor,
			@NonNull Consumer<String> callback
	) {
		if (Build.VERSION_CODES.S <= Build.VERSION.SDK_INT) {
			pm.getGroupOfPlatformPermission(permission, executor, callback::accept);
		} else {
			String group = getPermissionGroupOrNull(pm, permission);
			// Emulate async behaviour of getGroupOfPlatformPermission.
			executor.execute(() -> callback.accept(group));
		}
	}

	private static @Nullable CharSequence inferPermissionGroupLabel(
			@NonNull PackageManager pm,
			@NonNull String permission,
			@Nullable String group
	) {
		// Assumption is that we have `android.group.FOO` here, so only keeping "FOO" for the user.
		String fakeName = permission.substring(permission.lastIndexOf('.') + 1);
		if (group == null || "android.permission-group.UNDEFINED".equals(group)) {
			try {
				// API 29-30; see https://stackoverflow.com/a/69053542/253468.
				PermissionInfo permissionInfo = pm.getPermissionInfo(permission, 0);
				return fakeName + " - " + permissionInfo.loadLabel(pm);
			} catch (PackageManager.NameNotFoundException ex) {
				LOG.warn("Permission not found: {}", permission, ex);
				return fakeName;
			}
		} else {
			try {
				// API 21-28, 31+; see https://stackoverflow.com/a/69053542/253468.
				PermissionGroupInfo groupInfo = pm.getPermissionGroupInfo(group, 0);
				return groupInfo.loadLabel(pm);
			} catch (PackageManager.NameNotFoundException ex) {
				LOG.warn("Permission group not found: {}", group, ex);
				return fakeName;
			}
		}
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
