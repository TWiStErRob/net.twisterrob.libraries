package net.twisterrob.android.activity;

import java.io.*;

import org.slf4j.*;

import android.Manifest;
import android.animation.*;
import android.annotation.*;
import android.content.*;
import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.*;
import android.net.Uri;
import android.os.*;
import android.os.Build.*;
import android.provider.MediaStore;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.bumptech.glide.*;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.*;
import com.bumptech.glide.request.target.*;

import androidx.activity.ComponentActivity;
import androidx.annotation.*;
import androidx.core.app.ActivityCompat;

import net.twisterrob.android.capture_image.R;
import net.twisterrob.android.content.FileUriExposedException;
import net.twisterrob.android.utils.tools.CameraTools;
import net.twisterrob.android.content.CaptureImageFileProvider;
import net.twisterrob.android.utils.tools.CropTools;
import net.twisterrob.android.view.ExternalPicker;
import net.twisterrob.android.content.glide.*;
import net.twisterrob.android.permissions.PermissionProtectedAction;
import net.twisterrob.android.utils.concurrent.Callback;
import net.twisterrob.android.utils.tools.DialogTools;
import net.twisterrob.android.utils.tools.IntentTools;
import net.twisterrob.android.utils.tools.IOTools;
import net.twisterrob.android.view.*;
import net.twisterrob.android.view.CameraPreview.*;
import net.twisterrob.android.view.SelectionView.SelectionStatus;

/**
 * TODO check how others did it
 * <a href="https://github.com/lvillani/android-cropimage/tree/678f453d577232bbeed6b025dace823fa6bee43b">Crop Image from Gallery (as was 2014)</a>
 * <br>
 * <a href="http://adblogcat.com/a-camera-preview-with-a-bounding-box-like-google-goggles/">A camera preview with a bounding box like Google goggles</a>
 * > <a href="http://mobile.mymasterpeice.comxa.com/wp-content/uploads/2015/10/adblogcat.com-A-camera-preview-with-a-bounding-box-like-Google-goggles.pdf">as PDF</a>
 * > <a href="https://code.google.com/archive/p/ece301-examples/downloads">Downloads</a>
 * > <a href="https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/ece301-examples/CameraPreview.zip">CameraPreview.zip</a> (password preview).
 */
@UiThread
public class CaptureImage extends ComponentActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
	private static final Logger LOG = LoggerFactory.getLogger(CaptureImage.class);
	public static final String EXTRA_OUTPUT = MediaStore.EXTRA_OUTPUT;
	/**
	 * Extra in the resulting intent {@code data} in {@link #onActivityResult} when {@link #RESULT_CANCELED}.
	 * Type: Throwable, might be {@code null}, the problem is unknown.
	 */
	public static final String EXTRA_ERROR = "error";
	public static final String EXTRA_MAXSIZE = MediaStore.EXTRA_SIZE_LIMIT;
	public static final String EXTRA_QUALITY = "quality";
	public static final String EXTRA_FORMAT = "format";
	public static final String EXTRA_ASPECT = "keepAspect";
	public static final String EXTRA_SQUARE = "isSquare";
	public static final String EXTRA_FLASH = "flash";
	public static final String EXTRA_PICK = "pickImage";
	private static final String PREF_FLASH = EXTRA_FLASH;
	private static final String PREF_DENIED = "camera_permission_declined";
	private static final String KEY_STATE = "activityState";
	private static final String STATE_CAPTURING = "capturing";
	private static final String STATE_CROPPING = "cropping";
	private static final String STATE_PICKING = "picking";
	private static final float DEFAULT_MARGIN = 0.10f;
	private static final boolean DEFAULT_FLASH = false;
	public static final @Px int EXTRA_MAXSIZE_NO_MAX = CropTools.MAX_SIZE_NO_MAX;

	private SharedPreferences prefs;

	private CameraPreview mPreview;
	/**
	 * If we set {@code mPreview.setVisibility(INVISIBLE)}, the camera is released. Acquiring it again takes ~1 second.
	 * When the user is taking an image, but not satisfied with it, starting the camera again is an unnecessary delay.
	 * To prevent this delay: don't hide {@code mPreview}, but draw over it by toggling the visibility of this view.
	 */
	private View mPreviewHider;
	private SelectionView mSelection;
	private Uri mOutputUri;
	private File mSavedFile;
	private ImageView mImage;
	private View controls;
	private String state;

	private ImageButton mBtnCapture;
	private ImageButton mBtnPick;
	private ImageButton mBtnCrop;
	private ToggleButton mBtnFlash;
	private ExternalPicker mExternalPicker;

	private final PermissionProtectedAction restartPreview = new PermissionProtectedAction(
			this,
			new String[] {Manifest.permission.CAMERA},
			new PermissionProtectedAction.PermissionEvents() {
				@Override public void userInteraction() {
					mPreview.setVisibility(View.INVISIBLE);
				}
				@Override public void granted(@NonNull GrantedReason reason) {
					prefs.edit().remove(PREF_DENIED).apply();
					doRestartPreview();
				}
				@Override public void denied(@NonNull DeniedReason reason) {
					prefs.edit().putBoolean(PREF_DENIED, true).apply();
					// Camera: denied, go to Pick, select Take Picture, Cancel Camera rationale.
					enableControls();
				}
				@Override public void showRationale(@NonNull RationaleContinuation continuation) {
					DialogTools
							.confirm(CaptureImage.this, value -> {
								if (Boolean.TRUE.equals(value)) {
									continuation.rationaleAcceptedRetryRequest();
								} else {
									continuation.rationaleRejectedCancelProcess();
								}
							})
							.setTitle(R.string.image__permissions__camera_title)
							.setMessage(R.string.image__permissions__camera_message)
							.show();
				}
			}
	);

	@SuppressLint("InlinedApi")
	private final PermissionProtectedAction pick = new PermissionProtectedAction(
			this,
			VERSION.SDK_INT <= VERSION_CODES.P
					? new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}
					: new String[0],
			new PermissionProtectedAction.PermissionEvents() {
				@Override public void granted(@NonNull GrantedReason reason) {
					doPick();
				}
				@Override public void showRationale(@NonNull RationaleContinuation continuation) {
					DialogTools
							.confirm(CaptureImage.this, value -> {
								if (Boolean.TRUE.equals(value)) {
									continuation.rationaleAcceptedRetryRequest();
								} else {
									continuation.rationaleRejectedCancelProcess();
								}
							})
							.setTitle(R.string.image__permissions__disk_read_title)
							.setMessage(R.string.image__permissions__disk_read_message)
							.show();
				}
			}
	);

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// FIXME fast 180 rotation results in flipped image: http://stackoverflow.com/a/19599599/253468
		StrictMode.ThreadPolicy originalPolicy = StrictMode.allowThreadDiskWrites();
		try {
			prefs = getPreferences(MODE_PRIVATE);
		} finally {
			StrictMode.setThreadPolicy(originalPolicy);
		}

		mOutputUri = IntentTools.getParcelableExtra(getIntent(), EXTRA_OUTPUT, Uri.class);
		File externalPickerOutputFile;
		if (mOutputUri == null) {
			LOG.warn("Missing Uri typed extra: CaptureImage.EXTRA_OUTPUT, cancelling capture.");
			doReturn();
			return;
		} else {
			StrictMode.ThreadPolicy originalPolicy2 = StrictMode.allowThreadDiskWrites();
			try {
				externalPickerOutputFile = CaptureImageFileProvider.getTempFile(this, "capture.jpg");
				if (savedInstanceState == null) {
					LOG.trace("Clear image at {}", externalPickerOutputFile);
					// D/StrictMode: StrictMode policy violation; ~duration=33 ms: android.os.strictmode.DiskWriteViolation
					//noinspection ResultOfMethodCallIgnored best effort, try to prevent leaking old image
					externalPickerOutputFile.delete();
				}
			} finally {
				StrictMode.setThreadPolicy(originalPolicy2);
			}
		}
		setContentView(R.layout.activity_camera);
		controls = findViewById(R.id.controls);
		final View cameraControls = findViewById(R.id.camera_controls);
		mBtnPick = controls.findViewById(R.id.btn_pick);
		mBtnCapture = controls.findViewById(R.id.btn_capture);
		mBtnCrop = controls.findViewById(R.id.btn_crop);
		mBtnFlash = cameraControls.findViewById(R.id.btn_flash);
		mPreview = findViewById(R.id.preview);
		mPreviewHider = findViewById(R.id.previewHider);
		mImage = findViewById(R.id.image);
		mSelection = findViewById(R.id.selection);

		mPreview.addListener(new LoggingCameraPreviewListener());
		mPreview.addListener(new CameraPreviewListener() {
			@Override public void onCreate(CameraPreview preview) {
				if (Boolean.TRUE.equals(preview.isFlashSupported())) {
					mBtnFlash.setVisibility(View.VISIBLE);
					boolean isChecked = getInitialFlashEnabled();
					// Calls setOnCheckedChangeListener when it changed only, so force it.
					mBtnFlash.setChecked(!isChecked);
					mBtnFlash.setChecked(isChecked);
				} else {
					mBtnFlash.setVisibility(View.GONE);
				}
			}
			@Override public void onResume(CameraPreview preview) {
				cameraControls.setVisibility(View.VISIBLE);
			}
			@TargetApi(VERSION_CODES.HONEYCOMB)
			@Override public void onShutter(CameraPreview preview) {
				final View flashView = mSelection;
				if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
					ObjectAnimator whiteFlashIn = ObjectAnimator.ofObject(flashView,
							"backgroundColor", new ArgbEvaluator(), 0x00FFFFFF, 0xAAFFFFFF);
					ObjectAnimator whiteFlashOut = ObjectAnimator.ofObject(flashView,
							"backgroundColor", new ArgbEvaluator(), 0xAAFFFFFF, 0x00000000);
					whiteFlashIn.setDuration(200);
					whiteFlashOut.setDuration(300);
					AnimatorSet whiteFlash = new AnimatorSet();
					whiteFlash.playSequentially(whiteFlashIn, whiteFlashOut);
					whiteFlash.addListener(new AnimatorListenerAdapter() {
						@SuppressWarnings({"deprecation", "RedundantSuppression"}) 
						@Override public void onAnimationEnd(Animator animation) {
							flashView.setBackgroundDrawable(null);
						}
					});
					whiteFlash.start();
				}
			}
			@Override public void onPause(CameraPreview preview) {
				cameraControls.setVisibility(View.INVISIBLE);
			}
			@Override public void onDestroy(CameraPreview preview) {
				// Don't do mPreview.removeListener(this); because picking from gallery destroys.
			}
		});

		mSelection.setKeepAspectRatio(getIntent().getBooleanExtra(EXTRA_ASPECT, false));
		if (getIntent().getBooleanExtra(EXTRA_SQUARE, false)) {
			mSelection.setSelectionMarginSquare(DEFAULT_MARGIN);
		} else {
			mSelection.setSelectionMargin(DEFAULT_MARGIN);
		}

		mBtnFlash.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mPreview.setFlash(isChecked);
				prefs.edit().putBoolean(PREF_FLASH, isChecked).apply();
			}
		});

		mBtnCapture.setOnClickListener(new CaptureClickListener());
		mBtnPick.setOnClickListener(new PickClickListener());
		mBtnCrop.setOnClickListener(new CropClickListener());
		mExternalPicker = new ExternalPicker(
				this,
				mBtnPick,
				CaptureImageFileProvider.getUriForFile(this, externalPickerOutputFile),
				new ExternalPicker.Events() {
					@Override public void onCancelled() {
						mSelection.setSelectionStatus(SelectionStatus.BLURRY);
						enableControls();
					}
					@Override public void itemSelected() {
						disableControls();
					}
					@Override public void onGetContent(@NonNull Uri result) {
						onResult(result);
					}
					@Override public void onPick(@NonNull Uri result) {
						onResult(result);
					}
					@Override public void onPickVisualImage(@NonNull Uri result) {
						onResult(result);
					}
					@Override public void onCapture(@NonNull Uri result) {
						onResult(result);
					}
				}
		);

		boolean hasCamera = CameraTools.canHasCamera(this);
		if (!hasCamera) {
			mBtnCapture.setVisibility(View.GONE);
		}
		if (savedInstanceState == null) {
			boolean userDeclined = hasCamera
					&& !CameraTools.hasCameraPermission(this)
					&& prefs.getBoolean(PREF_DENIED, false);
			if (getIntent().getBooleanExtra(EXTRA_PICK, false) // forcing an immediate pick
					|| !hasCamera // device doesn't have camera
					|| userDeclined // device has camera, but user explicitly declined the permission
			) {
				mBtnPick.post(mBtnPick::performClick);
			} else {
				mBtnCapture.post(mBtnCapture::performClick);
			}
		} else {
			state = savedInstanceState.getString(KEY_STATE);
			if (state != null) {
				switch (state) {
					case STATE_CAPTURING:
						mBtnCapture.post(mBtnCapture::performClick);
						break;
					case STATE_PICKING:
						// In case activity is being re-created with state, it will go on to onActivityResult.
						break;
					case STATE_CROPPING:
						mSavedFile = getSavedFile();
						mSelection.setSelectionStatus(SelectionStatus.FOCUSED);
						prepareCrop();
						break;
				}
			}
		}
	}
	@Override protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_STATE, state);
	}

	private void onResult(@NonNull Uri result) {
		StrictMode.ThreadPolicy originalPolicy = StrictMode.allowThreadDiskWrites();
		try {
			// TODO background thread once coroutines.
			File copyAsFile = getSavedFile();
			// Make a copy of the original Uri, so that we have a local file that we can manipulate.
			// The result might be a content:// Uri, a local file, or a network stream, we don't know.
			copyResultToTarget(this, result, Uri.fromFile(copyAsFile));
			// Only assign the variable if the copy was a success.
			mSavedFile = copyAsFile;
		} catch (IOException ex) {
			Toast.makeText(getApplicationContext(), "Cannot save image: " + ex.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			StrictMode.setThreadPolicy(originalPolicy);
		}
		prepareCrop();
		enableControls();
	}

	private static void copyResultToTarget(@NonNull Context context, @NonNull Uri result, @NonNull Uri target)
			throws IOException {
		try {
			LOG.trace("Loading image from {} to {}", result, target);
			InputStream resultStream = context.getContentResolver().openInputStream(result);
			OutputStream targetStream = context.getContentResolver().openOutputStream(target);
			IOTools.copyStream(resultStream, targetStream);
		} catch (IOException ex){
			LOG.error("Cannot grab data from {} into {}", result, target, ex);
			throw ex;
		}
	}

	private void prepareCrop() {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			mImage.post(new Runnable() {
				@Override public void run() {
					// TODO Glide load may fail if destroyed while this is happening
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !isDestroyed()) {
						prepareCrop();
					}
				}
			});
			return;
		}
		state = STATE_CROPPING;
		LOG.trace("Loading taken image to crop: {}", mSavedFile);
		// Use a special target that will adjust the size of the ImageView to wrap the image (adjustViewBounds).
		// The selection view's size will match this hence the user can only select part of the image.
		// Used as listener to know if it's the thumbnail load or not, also needs skipMemoryCache to work
		ThumbWrapViewTarget<Bitmap> target = new ThumbWrapViewTarget<>(new BitmapImageViewTarget(mImage) {
			@Override public void setDrawable(Drawable drawable) {
				if (drawable instanceof TransitionDrawable) {
					// TODEL see https://github.com/bumptech/glide/issues/943
					((TransitionDrawable)drawable).setCrossFadeEnabled(false);
				}
				super.setDrawable(drawable);
			}
		});

		final SelectionStatus oldStatus = mSelection.getSelectionStatus();
		mSelection.setSelectionStatus(SelectionStatus.FOCUSING);
		RequestListener<Object, Bitmap> visualFeedbackListener = new RequestListener<Object, Bitmap>() {
			@Override public boolean onException(Exception e,
					Object model, Target<Bitmap> target, boolean isFirstResource) {
				mSelection.setSelectionStatus(SelectionStatus.BLURRY);
				return false;
			}
			@Override public boolean onResourceReady(Bitmap resource,
					Object model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
				if (oldStatus == SelectionStatus.BLURRY) {
					mSelection.setSelectionStatus(SelectionStatus.BLURRY);
				} else {
					mSelection.setSelectionStatus(SelectionStatus.FOCUSED);
				}
				return false;
			}
		};
		mPreviewHider.setVisibility(View.VISIBLE);
		GenericRequestBuilder<File, ImageVideoWrapper, Bitmap, Bitmap> image = Glide
				.with(this)
				.load(mSavedFile)
				.asBitmap() // no matter the format, just a single frame of bitmap
				.diskCacheStrategy(DiskCacheStrategy.NONE) // no need to cache, it's on disk already
				.skipMemoryCache(true) // won't ever be loaded again, or if it is, probably contains different bytes
				//.placeholder(new ColorDrawable(Color.BLACK)) // immediately hide the preview to prevent weird jump
				.transform(new FitCenter(GlideHelpers.NO_POOL)) // make sure full image is visible
				;

		RequestListener<Object, Bitmap> listener = new MultiRequestListener<>(visualFeedbackListener, target);
		image
				.decoder(new NonPoolingImageVideoBitmapDecoder(DecodeFormat.PREFER_ARGB_8888))
				// don't lose quality (may be disabled to gain memory for crop)
				// need the special target/listener
				.thumbnail(image
						.clone() // inherit everything, but load lower quality
						.listener(target)
						.decoder(new NonPoolingImageVideoBitmapDecoder(DecodeFormat.PREFER_RGB_565))
						.sizeMultiplier(0.1f)
						.animate(android.R.anim.fade_in) // fade thumbnail in (=crossFade from background)
				)
				.listener(listener)
				.error(R.drawable.image_error)
				.animate(new BitmapCrossFadeFactory(150)) // fade from thumb to image
				.into(target)
		;
	}

	private boolean getInitialFlashEnabled() {
		boolean flash;
		if (getIntent().hasExtra(EXTRA_FLASH)) {
			flash = getIntent().getBooleanExtra(EXTRA_FLASH, DEFAULT_FLASH);
		} else {
			flash = prefs.getBoolean(PREF_FLASH, DEFAULT_FLASH);
		}
		return flash;
	}

	@WorkerThread
	protected void doSave(@Nullable byte... data) {
		File savedFile = getSavedFile();
		mSavedFile = save(savedFile, data) ? savedFile : null;
	}

	private void flipSelection() {
		if (Boolean.TRUE.equals(mPreview.isFrontFacing())) {
			Rect selection = mSelection.getSelection();
			int width = mSelection.getWidth();
			selection.left = width - selection.left;
			selection.right = width - selection.right;
			mSelection.setSelection(selection);
		}
	}
	@WorkerThread
	protected boolean doCrop(RectF rect) {
		try {
			if (mSavedFile != null) {
				int maxSize = getIntent().getIntExtra(EXTRA_MAXSIZE, EXTRA_MAXSIZE_NO_MAX);
				int quality = getIntent().getIntExtra(EXTRA_QUALITY, 85);
				CompressFormat format = IntentTools.getSerializableExtra(getIntent(), EXTRA_FORMAT, CompressFormat.class, CompressFormat.JPEG);
				CropTools.crop(mSavedFile, rect, maxSize, quality, format);
			}
			// This a is a bit tricky, we always say the crop succeeded, even if there's no file.
			// Next steps will also check and act accordingly.
			return true;
		} catch (Exception ex) {
			Toast.makeText(getApplicationContext(), "Cannot crop image: " + ex.getMessage(), Toast.LENGTH_LONG).show();
			LOG.warn("Cannot crop image file {}", mSavedFile, ex);
			return false;
		} catch (OutOfMemoryError ex) {
			// CONSIDER http://stackoverflow.com/a/26239077/253468, or other solution on the same question
			String message = "There's not enough memory to crop the image, sorry. Try a smaller selection.";
			LOG.warn(message, ex);
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			return false;
		}
	}
	protected void doRestartPreview() {
		if (STATE_CROPPING.equals(state)) {
			flipSelection();
		}
		state = STATE_CAPTURING;
		LOG.trace("Restarting preview");
		mPreviewHider.setVisibility(View.INVISIBLE);
		mPreview.setVisibility(View.VISIBLE);
		mSavedFile = null;
		mSelection.setSelectionStatus(SelectionStatus.NORMAL);
		mPreview.cancelTakePicture();
		Glide.clear(mImage);
		mImage.setImageDrawable(null); // remove Glide placeholder for the view to be transparent
		enableControls();
	}
	protected void doPick() {
		state = STATE_PICKING;
		mPreview.setVisibility(View.INVISIBLE);
		mSelection.setSelectionStatus(SelectionStatus.FOCUSING);
		mExternalPicker.show();
		// Execution continues in `ExternalPicker.Events.*()` methods.
	}

	protected void doReturn() {
		Intent result = new Intent();
		result.setData(mOutputUri);
		if (mSavedFile != null) {
			try {
				// TODO background thread once coroutines.
				copyResultToTarget(this, Uri.fromFile(mSavedFile), mOutputUri);
				result.setDataAndType(result.getData(), "image/jpeg");
				setResult(RESULT_OK, result);
			} catch (IOException ex) {
				result.putExtra(EXTRA_ERROR, ex);
				setResult(RESULT_CANCELED, result);
			}
		} else {
			setResult(RESULT_CANCELED, result);
		}
		finish();
	}

	/**
	 * @param jpegCallback @WorkerThread
	 */
	@UiThread
	protected @CheckResult boolean take(final Callback<byte[]> jpegCallback) {
		LOG.trace("Initiate taking picture {}", mPreview.isRunning());
		if (!mPreview.isRunning()) {
			return false;
		}
		mSelection.setSelectionStatus(SelectionStatus.FOCUSING);
		mPreview.takePicture(new CameraPictureListener() {
			@WorkerThread
			@Override public boolean onFocus(final boolean success) {
				LOG.trace("Auto-focus result: {}", success);
				//noinspection ResourceType post should be safe to call from background
				mSelection.post(new Runnable() {
					@UiThread
					public void run() {
						mSelection.setSelectionStatus(success? SelectionStatus.FOCUSED : SelectionStatus.BLURRY);
					}
				});
				return true; // take the picture even if not in focus
			}
			@WorkerThread
			@Override public void onTaken(@Nullable byte... image) {
				jpegCallback.call(image);
			}
		}, true);
		return true;
	}

	@AnyThread
	private @NonNull File getSavedFile() {
		StrictMode.ThreadPolicy originalPolicy = StrictMode.allowThreadDiskWrites();
		try {
			return CaptureImageFileProvider.getTempFile(this, "saved.jpg");
		} finally {
			StrictMode.setThreadPolicy(originalPolicy);
		}
	}

	/**
	 * @return whether the file was successfully saved.
	 */
	@WorkerThread
	private static boolean save(@NonNull File file, @Nullable byte... data) {
		if (data == null) {
			return false;
		}
		LOG.trace("Saving {} bytes to {}", data.length, file);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(data);
			out.flush();
			LOG.info("Raw image ({} bytes) saved at {}", data.length, file);
			return true;
		} catch (FileNotFoundException ex) {
			LOG.error("Cannot find file {}", file, ex);
			return false;
		} catch (IOException ex) {
			LOG.error("Cannot write file {}", file, ex);
			return false;
		} finally {
			IOTools.ignorantClose(out);
		}
	}

	private @NonNull RectF getPictureRect() {
		float width = mSelection.getWidth();
		float height = mSelection.getHeight();

		RectF selection = new RectF(mSelection.getSelection());
		selection.left = selection.left / width;
		selection.top = selection.top / height;
		selection.right = selection.right / width;
		selection.bottom = selection.bottom / height;
		selection.sort();
		return selection;
	}

	/** @param maxSize pixel size or {@link #EXTRA_MAXSIZE_NO_MAX} */
	public static @NonNull Intent saveTo(@NonNull Context context, @NonNull Uri target, @Px int maxSize) {
		if ("file".equals(target.getScheme())) {
			throw new FileUriExposedException(
					"File Uri is not supported, use a content Uri instead: " + target
							+ "\nSee https://developer.android.com/reference/android/os/FileUriExposedException for more.");
		}
		Intent intent = new Intent(context, CaptureImage.class);
		intent.putExtra(CaptureImage.EXTRA_OUTPUT, target);
		intent.putExtra(CaptureImage.EXTRA_MAXSIZE, maxSize);
		return intent;
	}

	private class PickClickListener implements OnClickListener {
		@Override public void onClick(View v) {
			pick.executeBehindPermissions();
		}
	}

	private class CaptureClickListener implements OnClickListener {
		@Override public void onClick(View v) {
			if (!mPreview.isRunning()) { // picked gallery, camera button -> enable preview
				restartPreview.executeBehindPermissions();
				return;
			}
			disableControls();
			if (mSavedFile == null) {
				if (!take(new Callback<byte[]>() {
					@Override public void call(@Nullable byte[] data) {
						doSave(data);
						flipSelection();
						prepareCrop();
						enableControls();
					}
				})) {
					String message = "Please enable camera before taking a picture.";
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				}
			} else {
				restartPreview.executeBehindPermissions();
			}
		}
	}

	private void enableControls() {
		// post, so everything has time to set up
		controls.post(new Runnable() {
			@Override public void run() {
				controls.setVisibility(View.VISIBLE);
			}
		});
	}
	private void disableControls() {
		// CONSIDER a grayscale colorfilter on the preview?
		controls.setVisibility(View.INVISIBLE);
	}

	private class CropClickListener implements OnClickListener {
		@Override public void onClick(View v) {
			final RectF selection = getPictureRect();
			Glide.clear(mImage); // free up memory for crop op
			if (mSavedFile != null) {
				StrictMode.ThreadPolicy originalThreadPolicy = StrictMode.allowThreadDiskWrites();
				try {
					if (doCrop(selection)) {
						doReturn();
					}
				} finally {
					StrictMode.setThreadPolicy(originalThreadPolicy);
				}
			} else {
				if (!take(new Callback<byte[]>() {
					@Override public void call(@Nullable byte[] data) {
						doSave(data);
						flipSelection();
						if (doCrop(selection)) {
							doReturn();
						}
					}
				})) {
					String message = "Please select or take a picture before cropping.";
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private static class ThumbWrapViewTarget<Z> extends WrapViewTarget<Z> implements RequestListener<Object, Z> {
		private boolean isThumbnail;
		public ThumbWrapViewTarget(ImageViewTarget<? super Z> target) {
			super(target);
		}
		@Override public void onLoadStarted(Drawable placeholder) {
			super.onLoadStarted(placeholder);
			isThumbnail = false;
		}
		@Override public boolean onResourceReady(Z resource, Object model, Target<Z> target,
				boolean isFromMemoryCache, boolean isFirstResource) {
			this.isThumbnail = isFirstResource;
			return false; // normal route, just capture arguments
		}
		@Override public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {
			super.onResourceReady(resource, glideAnimation);
			if (isThumbnail) {
				update(LayoutParams.MATCH_PARENT);
			}
		}
		@Override public boolean onException(Exception e, Object model, Target<Z> target,
				boolean isFirstResource) {
			return false; // go for onLoadFailed
		}
	}
}
