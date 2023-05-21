package net.twisterrob.android.content;

import java.io.File;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import net.twisterrob.android.activity.CaptureImage;
import net.twisterrob.android.capture_image.R;
import net.twisterrob.android.utils.tools.*;

// FIXME https://developer.android.com/guide/topics/providers/document-provider.html
public class ImageRequest {
	private static final Logger LOG = LoggerFactory.getLogger(ImageRequest.class);

	private final Activity activity;
	private final Intent intent;
	private final int requestCode;

	private ImageRequest(Intent intent, int requestCode, Activity activity) {
		this.intent = intent;
		this.requestCode = requestCode;
		this.activity = activity;
	}

	public Intent getIntent() {
		return intent;
	}

	public int getRequestCode() {
		return requestCode;
	}

	public void start() {
		if (activity != null) {
			start(activity);
		} else {
			throw new IllegalStateException("Create the builder with an Activity to be able to use start(), "
					+ "or use start(Activity).");
		}
	}

	public @Nullable Uri getPictureUriFromResult(int requestCode, int resultCode, Intent data) {
		Uri selectedImageUri = null;
		if (resultCode == Activity.RESULT_OK && requestCode == this.requestCode && data != null) {
			boolean isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
			if (isCamera) {
				selectedImageUri = IntentTools.getParcelableExtra(data, MediaStore.EXTRA_OUTPUT, Uri.class);
			} else {
				selectedImageUri = data.getData();
			}
		}
		return selectedImageUri;
	}

	public void start(Activity activity) {
		activity.startActivityForResult(getIntent(), getRequestCode());
	}

	public static class Builder {
		private static final short REQUEST_CODE_BASE = 0x4100;
		private static final short REQUEST_CODE_PICK = 1 << 1;
		private static final short REQUEST_CODE_TAKE = 1 << 2;
		private static final short REQUEST_CODE_CROP = 1 << 3;
		private final Context context;
		private final List<Intent> intents = new ArrayList<>();
		private final Intent chooserIntent;
		private boolean requestCodeSet = false;
		private int requestCode = REQUEST_CODE_BASE;
		public Builder(Context context) {
			this.context = context;
			String title = context.getString(R.string.image__choose_external__title);
			this.chooserIntent = Intent.createChooser(new Intent(CaptureImage.ACTION), title);
		}

		public Builder withRequestCode(int requestCode) {
			this.requestCode = requestCode;
			this.requestCodeSet = true;
			return this;
		}
		public Builder addGalleryIntent() {
			if (!requestCodeSet) {
				requestCode |= REQUEST_CODE_PICK;
			}
			intents.addAll(AndroidTools.resolveIntents(context, createGalleryIntent(), 0));
			return this;
		}
		public Builder addCameraIntents(File file) {
			if (!requestCodeSet) {
				requestCode |= REQUEST_CODE_TAKE;
			}
			intents.addAll(createCameraIntents(context, Uri.fromFile(file)));
			return this;
		}
		public Builder addCameraIntents(Uri uri) {
			if (!requestCodeSet) {
				requestCode |= REQUEST_CODE_TAKE;
			}
			intents.addAll(createCameraIntents(context, uri));
			return this;
		}

		public ImageRequest build() {
			Intent[] intents = buildInitialIntents();
			Arrays.sort(intents, new IntentByLabelComparator(context.getPackageManager()));
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);

			Activity activity = context instanceof Activity? (Activity)context : null;
			return new ImageRequest(chooserIntent, requestCode, activity);
		}
		private Intent[] buildInitialIntents() {
			Intent[] intents = this.intents.toArray(new Intent[0]);
			PackageManager pm = context.getPackageManager();
			for (int i = 0; i < intents.length; i++) {
				Intent intent = intents[i];
				if (!(intent instanceof LabeledIntent)) {
					ResolveInfo info = PackageManagerTools.resolveActivity(pm, intent, 0);
					if (info == null) {
						// Assumption is that all the intents used as the input are already resolved once.
						LOG.warn("Intent {} has no ResolveInfo.", intent);
						continue;
					}
					CharSequence appLabel = info.loadLabel(pm);
					CharSequence label;
					if (MediaStore.ACTION_IMAGE_CAPTURE.equals(intent.getAction())) {
						label = TextTools.formatFormatted(context,
								R.string.image__choose_external__intent_label_take, appLabel);
					} else if (Intent.ACTION_GET_CONTENT.equals(intent.getAction())) {
						label = TextTools.formatFormatted(context,
								R.string.image__choose_external__intent_label_pick, appLabel);
					} else {
						label = appLabel;
					}
					intent = new LabeledIntent(intent, null, TextTools.ensureString(label), 0);
				}
				intents[i] = intent;
			}
			return intents;
		}
	}

	public static void openImageInGallery(Activity activity, File sourceFile) {
		activity.startActivity(createOpenImageIntent(sourceFile));
	}

	private static Intent createOpenImageIntent(File sourceFile) {
		Uri base = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		Uri uri = base.buildUpon().appendPath(sourceFile.getAbsolutePath()).build();
		return new Intent(Intent.ACTION_VIEW, uri);
	}

	private static List<Intent> createCameraIntents(Context context, Uri outputFileUri) {
		if (!canLaunchCameraIntent(context)) {
			return Collections.emptyList();
		}
		if (!canHasCamera(context)) {
			return Collections.emptyList();
		}
		Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		List<Intent> cameraIntents = AndroidTools.resolveIntents(context, captureIntent, 0);
		for (Intent intent : cameraIntents) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		}
		return cameraIntents;
	}

	private static @NonNull Intent createGalleryIntent() {
		Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
		galleryIntent.setType("image/*");
		return galleryIntent;
	}

	public static boolean hasReadPermission(@NonNull Context context) {
		if (VERSION.SDK_INT < VERSION_CODES.KITKAT) {
			// This permission is enforced starting in API level 19.
			// Before API level 19, this permission is not enforced and
			// all apps still have access to read from external storage. 
			return true;
		}
		int permissionState = PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
		return permissionState == PermissionChecker.PERMISSION_GRANTED;
	}
	
	/**
	 * Check if the current {@code context} is able to launch {@link MediaStore#ACTION_IMAGE_CAPTURE}.
	 * 
	 * Yes, it is strange that the current context needs to have camera permission to launch a camera app.
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

	private static class IntentByLabelComparator implements Comparator<Intent> {
		private final @NonNull PackageManager pm;
		public IntentByLabelComparator(@NonNull PackageManager pm) {
			this.pm = pm;
		}
		@Override public int compare(@NonNull Intent lhs, @NonNull Intent rhs) {
			CharSequence lLabel = getLabel(lhs);
			CharSequence rLabel = getLabel(rhs);
			// Poor man's null-safe comparison.
			return String.valueOf(lLabel).compareTo(String.valueOf(rLabel));
		}
		private @Nullable CharSequence getLabel(@NonNull Intent intent) {
			if (intent instanceof LabeledIntent) {
				return ((LabeledIntent)intent).loadLabel(pm);
			} else {
				ResolveInfo info = PackageManagerTools.resolveActivity(pm, intent, 0);
				return info != null? info.loadLabel(pm) : null;
			}
		}
	}
}
