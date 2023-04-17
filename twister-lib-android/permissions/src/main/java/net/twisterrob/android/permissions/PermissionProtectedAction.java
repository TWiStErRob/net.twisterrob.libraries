package net.twisterrob.android.permissions;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.annotation.UiThread;

/**
 * Abstract away the complexity of permissions.
 *
 * <h3>Goals</h3>
 * <ul>
 *     <li>clean, small interface</li>
 *     <li>handle all API levels</li>
 *     <li>be compliant and handle quirks</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <ul>
 *     <li>Create and store an instance in the Activity's constructor or onCreate.</li>
 *     <li>Call {@link #executeBehindPermissions()}
 *         from the action or from {@link Activity#onCreate(Bundle)}.</li>
 * </ul>
 */
@SuppressWarnings("JavadocReference")
public class PermissionProtectedAction {
	private static final @NonNull Logger LOG = LoggerFactory.getLogger(PermissionProtectedAction.class);

	private final @NonNull PermissionsInterrogator interrogator;
	private final @NonNull PermissionStateCalculator stateCalculator;
	private final @NonNull PermissionEvents callback;
	@Size(min = 1)
	private final @NonNull String[] permissions;

	private final @NonNull ActivityResultLauncher<String[]> permissionRequestLauncher;

	public PermissionProtectedAction(
			@NonNull ComponentActivity requestHost,
			@Size(min = 1)
			@NonNull String[] permissions,
			@NonNull PermissionEvents callback
	) {
		this(requestHost, new PermissionsInterrogator(requestHost), new PermissionStateCalculator(requestHost), permissions, callback);
	}
	PermissionProtectedAction(
			@NonNull ActivityResultCaller requestHost,
			@NonNull PermissionsInterrogator interrogator,
			@NonNull PermissionStateCalculator stateCalculator,
			@Size(min = 1)
			@NonNull String[] permissions,
			@NonNull PermissionEvents callback
	) {
		this.permissionRequestLauncher = requestHost.registerForActivityResult(
				new RequestMultiplePermissions(), this::onActivityResult);
		this.interrogator = interrogator;
		this.stateCalculator = stateCalculator;
		this.permissions = permissions;
		this.callback = callback;
	}

	@AnyThread
	public @NonNull PermissionState currentState() {
		return stateCalculator.currentState(permissions);
	}

	@UiThread
	public void executeBehindPermissions() {
		if (interrogator.hasAllPermissions(permissions)) {
			grantedPermanently();
		}
		if (interrogator.needsAnyRationale(permissions)) {
			showRationale();
		}
		callback.userInteraction();
		requestPermissions();
	}

	private void requestPermissions() {
		permissionRequestLauncher.launch(permissions);
	}

	private void onActivityResult(Map<String, Boolean> isGranted) {
		if (isGranted.isEmpty()) {
			requestCancelled();
			return;
		}
		if (interrogator.isAllGranted(isGranted)) {
			grantedFirstTime();
			return;
		}
		// Some permissions were not granted (yet).
		if (interrogator.needsAnyRationale(permissions)) {
			deniedFirstTime();
			return;
		}
		deniedPermanently();
	}

	private void showRationale() {
		callback.userInteraction();
		callback.showRationale(new PermissionEvents.RationaleContinuation() {
			@Override public void rationaleAcceptedRetryRequest() {
				rationaleAccepted();
			}
			@Override public void rationaleRejectedCancelProcess() {
				rationaleRejected();
			}
		});
	}

	private void requestCancelled() {
		LOG.trace("Permission request cancelled, unknown permission state -> can't use feature.");
		callback.denied();
	}
	private void grantedFirstTime() {
		LOG.trace("Permission request granted -> continue with feature.");
		callback.granted();
	}
	private void grantedPermanently() {
		LOG.trace("Permission request not necessary, granted already -> continue with feature.");
		callback.granted();
	}
	private void deniedFirstTime() {
		LOG.trace("Permission request denied -> can't use feature until user tries again.");
		// TODO need to be able to callback.showRationale(new RetryAfterRationale()); from this point.
		callback.denied();
	}
	private void rationaleAccepted() {
		LOG.trace("Permission request rationale accepted -> request permissions again.");
		requestPermissions();
	}
	private void rationaleRejected() {
		LOG.trace("Permission request rationale rejected -> don't nag until user tries again.");
		callback.denied();
	}
	private void deniedPermanently() {
		LOG.trace("Permission request denied permanently -> can't use feature, coach user.");
		callback.denied();
	}

	@UiThread
	public interface PermissionEvents {

		default void userInteraction() {
			// Optional override.
		}

		void granted();

		default void denied() {
			// Nothing to do, user will try again.
		}

		default void showRationale(@NonNull RationaleContinuation continuation) {
			continuation.rationaleRejectedCancelProcess();
		}

		/**
		 * Callback for letting the user choose what to do after being shown a rationale.
		 */
		@UiThread
		interface RationaleContinuation {

			/**
			 * Call if the user chooses to continue, and wants to see the permission dialog again.
			 */
			void rationaleAcceptedRetryRequest();

			/**
			 * Call if the user dismisses the UI showing the rationale.
			 */
			void rationaleRejectedCancelProcess();
		}
	}
}
