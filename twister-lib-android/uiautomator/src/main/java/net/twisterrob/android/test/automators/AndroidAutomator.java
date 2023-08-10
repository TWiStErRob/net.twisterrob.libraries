package net.twisterrob.android.test.automators;

import android.content.*;
import android.os.Build.*;

import androidx.annotation.*;
import androidx.test.uiautomator.*;

import static androidx.test.platform.app.InstrumentationRegistry.*;

import net.twisterrob.android.annotation.IdResName;
import net.twisterrob.android.test.espresso.DialogMatchers;
import net.twisterrob.android.utils.tools.ResourceTools;

import static net.twisterrob.android.test.automators.UiAutomatorExtensions.*;

public class AndroidAutomator {

	public static final String PACKAGE_CHOOSER = "android";
	public static final String PACKAGE_SETTINGS = "com.android.settings";
	public static final String PACKAGE_MARKET = "com.android.vending";
	public static final String PACKAGE_PACKAGE_INSTALLER = "com.android.packageinstaller";
	public static final String PACKAGE_PERMISSION_CONTROLLER = "com.android.permissioncontroller";

	public static @IdResName String permissionAllow() {
		if (VERSION.SDK_INT <= VERSION_CODES.P) {
			return externalId(PACKAGE_PACKAGE_INSTALLER, "permission_allow_button");
		} else {
			return externalId(PACKAGE_PERMISSION_CONTROLLER, "permission_allow_button");
		}
	}

	public static @IdResName String permissionDeny() {
		if (VERSION.SDK_INT <= VERSION_CODES.P) {
			return externalId(PACKAGE_PACKAGE_INSTALLER, "permission_deny_button");
		} else {
			return externalId(PACKAGE_PERMISSION_CONTROLLER, "permission_deny_button");
		}
	}

	/**
	 * @return whether the allow button was tapped
	 */
	public static boolean allowPermissionsIfNeeded() throws UiObjectNotFoundException {
		if (VERSION_CODES.M <= VERSION.SDK_INT) {
			UiDevice device = UiDevice.getInstance(getInstrumentation());
			UiObject allow = device.findObject(new UiSelector().resourceId(permissionAllow()));
			if (allow.exists()) {
				shortClickOn(permissionAllow());
				return true;
			}
		}
		return false;
	}

	/**
	 * @return whether the deny button was tapped
	 */
	public static boolean denyPermissionsIfNeeded() throws UiObjectNotFoundException {
		if (VERSION_CODES.M <= VERSION.SDK_INT) {
			UiDevice device = UiDevice.getInstance(getInstrumentation());
			UiObject allow = device.findObject(new UiSelector().resourceId(permissionDeny()));
			if (allow.exists()) {
				shortClickOn(permissionDeny());
				return true;
			}
		}
		return false;
	}

	public static void acceptAnyPermissions() throws UiObjectNotFoundException {
		//noinspection StatementWithEmptyBody condition is an action which returns success.
		while (AndroidAutomator.allowPermissionsIfNeeded()) {
			// Keep accepting permissions until we get to the app.
		}
	}

	public static void launchApp(@NonNull String packageName) {
		Context context = getInstrumentation().getContext();
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		if (intent == null) {
			throw new IllegalArgumentException(packageName + " does not have a launchable intent");
		}
		if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		}
		context.startActivity(intent);
	}

	@RequiresApi(UiAutomatorExtensions.UI_AUTOMATOR_VERSION)
	public static void launchAppAndWait(@NonNull String packageName) {
		String previousPackageName = getCurrentAppPackageName();
		launchApp(packageName);
		waitForAnAppToBeForegrounded(previousPackageName);
	}

	@RequiresApi(UiAutomatorExtensions.UI_AUTOMATOR_VERSION)
	public static String getPositiveButtonLabel() throws UiObjectNotFoundException {
		return getText(UiAutomatorExtensions.androidId(DialogMatchers.BUTTON_POSITIVE));
	}

	@RequiresApi(UiAutomatorExtensions.UI_AUTOMATOR_VERSION)
	public static String getChooserTitle() throws UiObjectNotFoundException {
		int id = VERSION.SDK_INT >= VERSION_CODES.S
				? ResourceTools.getIDResourceID(null, "content_preview_filename")
				: android.R.id.title;
		return UiAutomatorExtensions.getText(UiAutomatorExtensions.androidId(id));
	}
}
