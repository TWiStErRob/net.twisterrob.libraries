package net.twisterrob.android.utils.tools;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public /*static*/ abstract class PackageManagerTools {

	private static final Logger LOG = LoggerFactory.getLogger(PackageManagerTools.class);

	protected PackageManagerTools() {
		// static utility class
	}

	@SuppressWarnings("deprecation")
	public static @NonNull List<ResolveInfo> queryIntentActivities(
			@NonNull PackageManager pm, @NonNull Intent intent, long flags) {
		if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
			return pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags));
		} else {
			return pm.queryIntentActivities(intent, (int)flags);
		}
	}

	@SuppressWarnings("deprecation")
	public static @Nullable ResolveInfo resolveActivity(
			@NonNull PackageManager pm, @NonNull Intent intent, long flags) {
		if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
			return pm.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(flags));
		} else {
			return pm.resolveActivity(intent, (int)flags);
		}
	}

	@SuppressWarnings("deprecation")
	public static @NonNull PackageInfo getPackageInfo(
			@NonNull PackageManager pm, @NonNull String packageName, long flags)
			throws PackageManager.NameNotFoundException {
		if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
			return pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags));
		} else {
			return pm.getPackageInfo(packageName, (int)flags);
		}
	}

	@SuppressWarnings("deprecation")
	public static @NonNull ActivityInfo getActivityInfo(
			@NonNull PackageManager pm, @NonNull ComponentName component, long flags)
			throws PackageManager.NameNotFoundException {
		if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
			return pm.getActivityInfo(component, PackageManager.ComponentInfoFlags.of(flags));
		} else {
			return pm.getActivityInfo(component, (int)flags);
		}
	}

	/** @see PackageManager#getActivityInfo(ComponentName, int) */
	public static @NonNull ActivityInfo getActivityInfo(@NonNull Activity activity, long flags) {
		try {
			PackageManager pm = activity.getPackageManager();
			return getActivityInfo(pm, activity.getComponentName(), flags);
		} catch (PackageManager.NameNotFoundException e) {
			LOG.warn("Activity doesn't exists, but has an instance? {}", activity, e);
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("deprecation")
	public static @NonNull List<PackageInfo> getInstalledPackages(
			@NonNull PackageManager pm, long flags) {
		if (Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT) {
			return pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(flags));
		} else {
			return pm.getInstalledPackages((int)flags);
		}
	}
}
