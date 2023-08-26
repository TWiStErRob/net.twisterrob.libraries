package net.twisterrob.android.content;

import android.Manifest;
import android.annotation.*;
import android.app.Activity;
import android.content.*;
import android.content.pm.*;
import android.net.Uri;
import android.os.Build.*;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;

import net.twisterrob.android.utils.tools.*;

public class ImageRequest {
	private final @NonNull Uri target;

	public ImageRequest(@NonNull Uri target) {
		this.target = target;
	}

	public @NonNull Intent createGetContent() {
		return new Intent(Intent.ACTION_GET_CONTENT)
				.addCategory(Intent.CATEGORY_OPENABLE)
				.setType("image/*");
	}

	public @NonNull Intent createPick() {
		return  new Intent(Intent.ACTION_PICK)
				.setType("image/*");
	}

	public @NonNull Intent createCaptureImage() {
		return new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
				.putExtra(MediaStore.EXTRA_OUTPUT, target);
	}

	public static @Nullable Uri getPictureUriFromResult(int expectedRequestCode, int requestCode, int resultCode, Intent data) {
		Uri selectedImageUri = null;
		if (resultCode == Activity.RESULT_OK && requestCode == expectedRequestCode && data != null) {
			boolean isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
			if (isCamera) {
				selectedImageUri = IntentTools.getParcelableExtra(data, MediaStore.EXTRA_OUTPUT, Uri.class);
			} else {
				selectedImageUri = data.getData();
			}
		}
		return selectedImageUri;
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
	@TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
	@SuppressWarnings("deprecation")
	public static boolean canHasCamera(Context context) {
		PackageManager pm = context.getPackageManager();
		boolean hasCameraAny = VERSION_CODES.JELLY_BEAN_MR1 < VERSION.SDK_INT;
		return android.hardware.Camera.getNumberOfCameras() > 0 && (
				(hasCameraAny && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
						|| pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
						|| pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
		);
	}
}
