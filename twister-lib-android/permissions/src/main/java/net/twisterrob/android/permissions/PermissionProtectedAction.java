package net.twisterrob.android.permissions;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
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
	private final @NonNull PermissionDenialRemediator denialRemediator;
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
		this.permissionRequestLauncher = requestHost.registerForActivityResult(
				new RequestMultiplePermissions(), this::onRequestPermissionsResult);
		this.interrogator = new PermissionsInterrogator(requestHost);
		this.stateCalculator = new PermissionStateCalculator(requestHost);
		this.denialRemediator =
				new PermissionDenialRemediator(requestHost, new RemediatorCallback());
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
			LOG.trace("Permission request not necessary, granted already -> continue with feature.");
			callback.granted(PermissionEvents.GrantedReason.PERMANENT);
			return;
		}
		if (interrogator.needsAnyRationale(permissions)) {
			showRationale();
			return;
		}
		callback.userInteraction();
		requestPermissions();
	}

	private void requestPermissions() {
		permissionRequestLauncher.launch(permissions);
	}

	private void onRequestPermissionsResult(Map<String, Boolean> isGranted) {
		if (isGranted.isEmpty()) {
			LOG.trace("Permission request cancelled, unknown permission state -> can't use feature.");
			callback.denied(PermissionEvents.DeniedReason.CANCELLED);
			return;
		}
		if (interrogator.isAllGranted(isGranted)) {
			LOG.trace("Permission request granted -> continue with feature.");
			callback.granted(PermissionEvents.GrantedReason.FIRST_TIME);
			return;
		}
		// Some permissions were not granted (yet).
		if (interrogator.needsAnyRationale(permissions)) {
			LOG.trace("Permission request denied -> can't use feature until user tries again.");
			// TODO need to be able to callback.showRationale(new RetryAfterRationale()); from this point.
			callback.denied(PermissionEvents.DeniedReason.FIRST_TIME);
			return;
		}
		denialRemediator.remediatePermanentDenial(permissions);
	}

	private void showRationale() {
		callback.userInteraction();
		callback.showRationale(new PermissionEvents.RationaleContinuation() {
			@Override public void rationaleAcceptedRetryRequest() {
				LOG.trace("Permission request rationale accepted -> request permissions again.");
				requestPermissions();
			}
			@Override public void rationaleRejectedCancelProcess() {
				LOG.trace("Permission request rationale rejected -> don't nag until user tries again.");
				callback.denied(PermissionEvents.DeniedReason.RATIONALE_REJECTED);
			}
		});
	}

	private class RemediatorCallback implements PermissionEvents.RationaleContinuation {
		@Override public void rationaleAcceptedRetryRequest() {
			if (interrogator.hasAllPermissions(permissions)) {
				LOG.trace("Permission request granted after going to settings.");
				callback.granted(PermissionEvents.GrantedReason.PERMANENT_REMEDIATION);
			} else {
				LOG.trace("Permission request denied after going to settings.");
				callback.denied(PermissionEvents.DeniedReason.PERMANENT);
			}
		}
		@Override public void rationaleRejectedCancelProcess() {
			LOG.trace("Permission request denied permanently -> can't use feature, coach user.");
			callback.denied(PermissionEvents.DeniedReason.PERMANENT_REMEDIATION_REJECTED);
		}
	}

	@UiThread
	public interface PermissionEvents {

		default void userInteraction() {
			// Optional override.
		}

		enum GrantedReason {
			FIRST_TIME,
			PERMANENT,
			PERMANENT_REMEDIATION,
		}
		void granted(@NonNull GrantedReason reason);

		enum DeniedReason {
			CANCELLED,
			FIRST_TIME,
			RATIONALE_REJECTED,
			PERMANENT,
			PERMANENT_REMEDIATION_REJECTED,
		}
		default void denied(@NonNull DeniedReason reason) {
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
