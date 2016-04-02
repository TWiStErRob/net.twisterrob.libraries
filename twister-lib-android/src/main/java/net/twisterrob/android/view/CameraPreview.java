package net.twisterrob.android.view;

import java.io.IOException;

import org.slf4j.*;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.*;
import android.os.*;
import android.util.AttributeSet;
import android.view.*;
import android.widget.Toast;

import net.twisterrob.android.utils.tools.AndroidTools;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	private static final Logger LOG = LoggerFactory.getLogger(CameraPreview.class);

	// TODEL EmptyMethod: https://youtrack.jetbrains.com/issue/IDEA-154073
	@SuppressWarnings("EmptyMethod")
	public interface CameraPreviewListener {
		void onStarted(CameraPreview preview);
		void onFinished(CameraPreview preview);
	}

	private CameraHandlerThread mCameraThread = null;
	private final MissedSurfaceEvents missedEvents = new MissedSurfaceEvents();
	private CameraHolder cameraHolder = null;
	private CameraPreviewListener listener = null;

	public CameraPreview(Context context, AttributeSet attributeset) {
		super(context, attributeset);
		LOG.trace("CameraPreview");

		getHolder().addCallback(this);
		initCompat();
	}

	@TargetApi(VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	private void initCompat() {
		if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
			getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	public void setListener(CameraPreviewListener listener) {
		this.listener = listener;
	}

	public @SuppressWarnings("deprecation") android.hardware.Camera getCamera() {
		return cameraHolder != null? cameraHolder.camera : null;
	}

	public boolean isRunning() {
		return cameraHolder != null;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		LOG.trace("surfaceCreated({}) {}", holder, cameraHolder != null);
		if (cameraHolder != null) {
			usePreview();
		} else {
			if (mCameraThread != null) {
				throw new IllegalStateException("Camera Thread already started");
			}
			mCameraThread = new CameraHandlerThread();
			mCameraThread.startOpenCamera();
			missedEvents.surfaceCreated(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		LOG.trace("surfaceChanged({}, format={}, w={}, h={}) {}", holder, format, w, h, cameraHolder != null);
		if (cameraHolder != null) {
			stopPreview();
			updatePreview(w, h);
			startPreview();
		} else {
			missedEvents.surfaceChanged(holder, format, w, h);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		LOG.trace("surfaceDestroyed({}) {}", holder, cameraHolder != null);
		if (cameraHolder != null) {
			stopPreview();
			releaseCamera();
		} else {
			missedEvents.surfaceDestroyed(holder);
		}
	}

	private void usePreview() {
		LOG.trace("Using preview {}", cameraHolder != null);
		try {
			if (cameraHolder != null) {
				LOG.trace("setPreviewDisplay {}", getHolder());
				cameraHolder.camera.setPreviewDisplay(getHolder());
			}
		} catch (RuntimeException | IOException ex) {
			LOG.error("Error setting up camera preview", ex);
		}
	}

	@SuppressWarnings("SuspiciousNameCombination")
	private void updatePreview(int w, int h) {
		LOG.trace("Updating preview {}", cameraHolder != null);
		if (cameraHolder == null) {
			return;
		}

		int width = getWidth();
		int height = getHeight();
		int degrees = AndroidTools.calculateRotation(getContext(), cameraHolder.cameraInfo);
		boolean landscape = degrees % 180 == 0;
		if (!landscape) {
			int temp = width;
			width = height;
			height = temp;
		}

		@SuppressWarnings("deprecation") android.hardware.Camera.Size previewSize =
				AndroidTools.getOptimalSize(cameraHolder.params.getSupportedPreviewSizes(), width, height);
		@SuppressWarnings("deprecation") android.hardware.Camera.Size pictureSize =
				AndroidTools.getOptimalSize(cameraHolder.params.getSupportedPictureSizes(), width, height);
		LOG.debug("orient: {}, size: {}x{} ({}), surface: {}x{} ({}), preview: {}x{} ({}), picture: {}x{} ({})", //
				degrees, //
				width, height, (float)width / (float)height, //
				w, h, (float)w / (float)h, //
				previewSize.width, previewSize.height, (float)previewSize.width / (float)previewSize.height, //
				pictureSize.width, pictureSize.height, (float)pictureSize.width / (float)pictureSize.height //
		);
		cameraHolder.params.setPreviewSize(previewSize.width, previewSize.height);
		cameraHolder.params.setPictureSize(pictureSize.width, pictureSize.height);
		cameraHolder.params.setRotation(degrees);
		cameraHolder.params.set("orientation", landscape? "landscape" : "portrait");
		cameraHolder.camera.setParameters(cameraHolder.params);
		cameraHolder.camera.setDisplayOrientation(degrees);
	}

	private void releaseCamera() {
		LOG.trace("Releasing camera {}", cameraHolder != null);
		if (cameraHolder != null) {
			// Important: Call release() to release the camera for use by other
			// applications. Applications should release the camera immediately
			// during onPause() and re-open() it during onResume()).
			LOG.info("Releasing {}", cameraHolder.camera);
			cameraHolder.camera.release();
			finished();
		}
		if (mCameraThread != null) {
			mCameraThread.stopThread();
			mCameraThread = null;
		}
	}

	private void startPreview() {
		LOG.trace("Starting preview {}", cameraHolder != null);
		try {
			if (cameraHolder != null) {
				cameraHolder.camera.startPreview();
			}
		} catch (RuntimeException ex) {
			LOG.error("Error starting camera preview", ex);
		}
	}

	private void stopPreview() {
		LOG.trace("Stopping preview {}", cameraHolder != null);
		try {
			if (cameraHolder != null) {
				cameraHolder.camera.stopPreview();
			}
		} catch (RuntimeException ex) {
			LOG.warn("ignore: tried to stop a non-existent preview", ex);
		}
	}

	private void started(CameraHolder holder) {
		cameraHolder = holder;
		missedEvents.replay();
		if (listener != null) {
			listener.onStarted(this);
		}
	}

	private void finished() {
		cameraHolder = null;
		if (listener != null) {
			listener.onFinished(this);
		}
	}

	public void takePicture(@SuppressWarnings("deprecation") android.hardware.Camera.PictureCallback jpegCallback) {
		LOG.trace("Taking picture {}", cameraHolder != null);
		if (cameraHolder == null) {
			return;
		}
		cameraHolder.camera.takePicture(null, null, null, jpegCallback);
	}

	public void cancelTakePicture() {
		LOG.trace("Initiate cancel take picture");
		mCameraThread.mHandler.post(new Runnable() {
			public void run() {
				LOG.trace("Cancel take picture");
				cancelAutoFocus();
				startPreview();
			}
		});
	}

	private void cancelAutoFocus() {
		LOG.trace("Cancel auto-focus {}", cameraHolder != null);
		if (cameraHolder != null) {
			cameraHolder.camera.cancelAutoFocus();
		}
	}

	@SuppressWarnings("deprecation")
	public void setCameraFocus(android.hardware.Camera.AutoFocusCallback autoFocus) {
		LOG.trace("Camera focus {}", cameraHolder != null);
		if (cameraHolder == null) {
			return;
		}
		String focusMode = cameraHolder.camera.getParameters().getFocusMode();
		if (android.hardware.Camera.Parameters.FOCUS_MODE_AUTO.equals(focusMode)
				|| android.hardware.Camera.Parameters.FOCUS_MODE_MACRO.equals(focusMode)) {
			cameraHolder.camera.autoFocus(autoFocus);
		} else {
			autoFocus.onAutoFocus(true, cameraHolder.camera);
		}
	}

	public void setFlash(boolean flash) {
		if (cameraHolder == null) {
			return;
		}
		@SuppressWarnings("deprecation")
		String flashMode = flash
				? android.hardware.Camera.Parameters.FLASH_MODE_ON
				: android.hardware.Camera.Parameters.FLASH_MODE_OFF;
		cameraHolder.params.setFlashMode(flashMode);
		cameraHolder.camera.setParameters(cameraHolder.params);
	}

	@SuppressWarnings("deprecation")
	private static class CameraHolder {
		final int cameraID;
		final android.hardware.Camera camera;
		final android.hardware.Camera.CameraInfo cameraInfo;
		final android.hardware.Camera.Parameters params;

		public CameraHolder(int id) {
			cameraID = id;
			LOG.trace("Opening camera");
			camera = android.hardware.Camera.open(cameraID);
			LOG.trace("Opened camera");
			try {
				LOG.trace("setPreviewDisplay null");
				camera.setPreviewDisplay(null);
			} catch (RuntimeException | IOException ex) {
				LOG.error("Error setting up camera preview", ex);
			}
			cameraInfo = new android.hardware.Camera.CameraInfo();
			params = camera.getParameters();
			android.hardware.Camera.getCameraInfo(cameraID, cameraInfo);
		}
	}

	private class CameraHandlerThread extends HandlerThread {
		private Handler mHandler = null;

		CameraHandlerThread() {
			super("CameraHandlerThread");
			start();
			mHandler = new Handler(getLooper());
		}

		void startOpenCamera() {
			mHandler.post(new Runnable() {
				@Override
				public void run() { // on Camera's Looper
					try {
						final CameraHolder holder = new CameraHolder(findCamera());
						CameraPreview.this.post(new Runnable() {
							public void run() { // on UI Looper
								CameraPreview.this.started(holder);
							}
						});
					} catch (RuntimeException ex) {
						LOG.error("Error setting up camera", ex);
						Toast.makeText(CameraPreview.this.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
					}
				}

				private int findCamera() {
					return 0; // TODO front camera?
				}
			});
		}

		void stopThread() {
			getLooper().quit();
		}
	}

	private class MissedSurfaceEvents implements SurfaceHolder.Callback {
		private boolean surfaceCreated;
		private boolean surfaceChanged;
		private boolean surfaceDestroyed;
		private SurfaceHolder holder;
		private int format;
		private int w, h;

		public void surfaceCreated(SurfaceHolder holder) {
			this.surfaceCreated = true;
			this.holder = holder;
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			this.surfaceChanged = true;
			this.holder = holder;
			this.format = format;
			this.w = width;
			this.h = height;
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			this.surfaceDestroyed = true;
			this.holder = holder;
		}

		public void replay() {
			if (surfaceDestroyed) {
				CameraPreview.this.surfaceDestroyed(holder);
				return;
			}
			if (surfaceCreated) {
				CameraPreview.this.surfaceCreated(holder);
			}
			if (surfaceChanged) {
				CameraPreview.this.surfaceChanged(holder, format, w, h);
			}
		}
	}
}
