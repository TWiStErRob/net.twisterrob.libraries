package net.twisterrob.android.utils.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;

public final class CameraTools {
	private CameraTools() {
		throw new InternalError("Utility class cannot be instantiated.");
	}

	/**
	 * Check if the current {@code context} is able to launch {@link MediaStore#ACTION_IMAGE_CAPTURE}.
	 * <br>
	 * Yes, it is strange that the current context needs to have camera permission to launch a camera app:
	 * <pre>java.lang.SecurityException: Permission Denial:
	 * starting Intent { act=android.media.action.IMAGE_CAPTURE flg=0x3
	 * cmp=com.google.android.GoogleCamera/com.android.camera.activity.CaptureActivity
	 * clip={text/uri-list hasLabel(0) {U(content)}} (has extras) }
	 * from ProcessRecord{6a8a5c1 3152:net.twisterrob.inventory.debug/u0a327} (pid=3152, uid=10327)
	 * with revoked permission android.permission.CAMERA
	 * </pre>
	 */
	public static boolean canLaunchCameraIntent(@NonNull Context context) {
		return VERSION.SDK_INT < VERSION_CODES.M
				|| !AndroidTools.getDeclaredPermissions(context).contains(Manifest.permission.CAMERA)
				|| hasCameraPermission(context);
	}

	public static boolean hasCameraPermission(@NonNull Context context) {
		int permissionState = PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA);
		return permissionState == PermissionChecker.PERMISSION_GRANTED;
	}

	@SuppressLint("UnsupportedChromeOsCameraSystemFeature") // REPORT it is checked right before
	@SuppressWarnings("deprecation")
	public static boolean canHasCamera(@NonNull Context context) {
		PackageManager pm = context.getPackageManager();
		boolean hasCameraAny = VERSION_CODES.JELLY_BEAN_MR1 < VERSION.SDK_INT;
		return android.hardware.Camera.getNumberOfCameras() > 0 && (
				(hasCameraAny && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
						|| pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
						|| pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
		);
	}
}
