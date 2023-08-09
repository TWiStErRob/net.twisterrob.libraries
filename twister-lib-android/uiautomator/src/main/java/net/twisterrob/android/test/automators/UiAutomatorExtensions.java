package net.twisterrob.android.test.automators;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssume.*;
import static org.junit.Assert.*;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.UiAutomation;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build.*;
import android.view.Surface;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.*;
import androidx.test.uiautomator.*;

import static androidx.test.platform.app.InstrumentationRegistry.*;

import net.twisterrob.android.annotation.*;
import net.twisterrob.android.test.espresso.DialogMatchers;

/**
 * {@link RawRes} is intentionally misused in this class.
 * It signifies a normal string of pointing to no particular type of resource, but containing a value of a resource. 
 */
public class UiAutomatorExtensions {
	private static final Logger LOG = LoggerFactory.getLogger(UiAutomatorExtensions.class);

	public static final int UI_AUTOMATOR_VERSION = VERSION_CODES.JELLY_BEAN_MR2;

	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static String getText(@IdResName String id) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiObject object = device.findObject(new UiSelector().resourceId(id));
		return object.getText();
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void setText(@IdResName String id, String value) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiObject object = device.findObject(new UiSelector().resourceId(id));
		assertTrue("expected to set text", object.setText(value));
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static boolean exists(@IdResName String id) {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiObject object = device.findObject(new UiSelector().resourceId(id));
		return object.exists();
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void clickOnBottomRight(@IdResName String id) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiObject object = device.findObject(new UiSelector().resourceId(id));
		assertTrue("expected to click and new window appear", object.clickBottomRight());
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void clickOn(@NonNull UiObject object) throws UiObjectNotFoundException {
		assertTrue("expected to click and new window appear", object.clickAndWaitForNewWindow());
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void clickOn(@IdResName String id) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		clickOn(device.findObject(new UiSelector().resourceId(id)));
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void clickOnLabel(@NonNull String label) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		clickOn(device.findObject(new UiSelector().text(label)));
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void clickOnDescription(@IdResName String id) throws UiObjectNotFoundException {
		clickOnDescriptionLabel(getText(id));
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void clickOnDescriptionLabel(String label) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		clickOn(device.findObject(new UiSelector().description(label)));
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void shortClickOn(@IdResName String id) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		shortClickOn(device.findObject(new UiSelector().resourceId(id)));
	}
	public static void shortClickOn(@NonNull UiObject object) throws UiObjectNotFoundException {
		assertTrue("expected to click", object.click());
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void shortClickOnLabel(@NonNull String label) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		shortClickOn(device.findObject(new UiSelector().text(label)));
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void shortClickOnDescriptionLabel(String label) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		shortClickOn(device.findObject(new UiSelector().description(label)));
	}
	public static @IdResName String androidId(@IdRes int resId) {
		return getInstrumentation().getContext().getResources().getResourceName(resId);
	}
	public static @IdResName String internalId(@IdRes int resId) {
		return getInstrumentation().getTargetContext().getResources().getResourceName(resId);
	}
	public static @IdResName String externalId(String packageName, @StringResName String resName) {
		return packageName + ":id/" + resName;
	}

	/**
	 * Using this method is better than hardcoding external strings.
	 * Hopefully the internal keys are more stable than the user facing content.
	 */
	public static String externalString(String packageName, @StringResName String resName,
			String englishFallback)
			throws NameNotFoundException {
		// CONSIDER using ResourceTools.getStringResourceID, but it needs more overloads.
		Resources res = getInstrumentation().getContext().getPackageManager().getResourcesForApplication(packageName);
		@SuppressLint("DiscouragedApi") // It's external packages, no other way.
		@StringRes int resId = res.getIdentifier(resName, "string", packageName);

		String resValue;
		if (resId != 0) {
			resValue = res.getString(resId);
		} else {
			String warning = String.format("Missing resource: @%s:string/%s", packageName, resName);
			assumeThat(warning + "; can't use English fallback",
					Locale.getDefault().getLanguage(), is("en"));
			LOG.warn(warning + "; using English fallback: " + englishFallback);
			resValue = englishFallback;
		}
		return resValue;
	}

	@RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
	private static void clickInExternalDialog(@IdRes int buttonId) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiObject dialogButton = device.findObject(new UiSelector().resourceId(androidId(buttonId)));
		assertTrue("expected to click and new window appear", dialogButton.clickAndWaitForNewWindow());
	}
	@RequiresApi(UiAutomatorExtensions.UI_AUTOMATOR_VERSION)
	public static void clickPositiveInExternalDialog() throws UiObjectNotFoundException {
		clickInExternalDialog(DialogMatchers.BUTTON_POSITIVE);
	}
	@RequiresApi(UiAutomatorExtensions.UI_AUTOMATOR_VERSION)
	public static void clickNegativeInExternalDialog() throws UiObjectNotFoundException {
		clickInExternalDialog(DialogMatchers.BUTTON_NEGATIVE);
	}
	@RequiresApi(UiAutomatorExtensions.UI_AUTOMATOR_VERSION)
	public static void clickNeutralInExternalDialog() throws UiObjectNotFoundException {
		clickInExternalDialog(DialogMatchers.BUTTON_NEUTRAL);
	}

	@RequiresApi(VERSION_CODES.JELLY_BEAN)
	public static void pressBackExternal() {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		assertTrue("expected to press Back button", device.pressBack());
	}

	@RequiresApi(VERSION_CODES.JELLY_BEAN)
	public static void pressBackExternalUnsafe() {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		device.pressBack();
	}

	@RequiresApi(VERSION_CODES.JELLY_BEAN)
	public static String getCurrentAppPackageName() {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		return device.getCurrentPackageName();
	}

	@RequiresApi(VERSION_CODES.JELLY_BEAN)
	public static void waitForAppToBeBackgrounded() {
		final long timeout = TimeUnit.SECONDS.toMillis(10);

		UiDevice device = UiDevice.getInstance(getInstrumentation());
		BySelector appPackage = By.pkg(getInstrumentation().getTargetContext().getPackageName()).depth(0);
		assertTrue("expected " + appPackage + " to disappear", device.wait(Until.gone(appPackage), timeout));
	}

	@RequiresApi(VERSION_CODES.JELLY_BEAN)
	public static void waitForAppToBeForegrounded(@NonNull String packageName) {
		final long timeout = TimeUnit.SECONDS.toMillis(10);

		UiDevice device = UiDevice.getInstance(getInstrumentation());
		BySelector appPackage = By.pkg(packageName).depth(0);
		assertTrue("expected " + appPackage + " to appear", device.wait(Until.hasObject(appPackage), timeout));
	}

	/**
	 * @param previousPackageName a saved value from {@link #getCurrentAppPackageName()} before an action was performed
	 */
	@RequiresApi(VERSION_CODES.JELLY_BEAN)
	public static void waitForAnAppToBeForegrounded(@NonNull String previousPackageName) {
		final long timeout = TimeUnit.SECONDS.toMillis(10);

		UiDevice device = UiDevice.getInstance(getInstrumentation());
		BySelector appPackage = By.pkg(previousPackageName).depth(0);
		assertTrue("expected an app other than " + appPackage + " to appear",
				device.wait(Until.gone(appPackage), timeout));
	}

	@RequiresApi(VERSION_CODES.LOLLIPOP)
	public static void ensureNoSoftKeyboard() throws TimeoutException {
		while (isSoftKeyboardShown()) {
			LOG.info("Soft keyboard is shown, attempting to closing it with a Back button press.");
			UiAutomation automation = getInstrumentation().getUiAutomation();
			UiAutomatorExtensions.pressBackExternalUnsafe();
			// Wait a bit so the back button press has a chance to take effect.
			// Tried with below code as well, but there were no events fired in response to the back press.
			// automation.executeAndWaitForEvent(::pressBackUnsafe) { e.eventType == TYPE_WINDOW_STATE_CHANGED }
			automation.waitForIdle(100, 10000);
		}
	}

	@RequiresApi(VERSION_CODES.LOLLIPOP)
	public static boolean isSoftKeyboardShown() throws TimeoutException {
		UiAutomation automation = getInstrumentation().getUiAutomation();
		// Wait a bit, so if there's a soft keyboard launching, it has a chance to show up.
		automation.waitForIdle(1000, 10000);
		// Ensure the UiAutomation is configured correctly so getWindows() returns something.
		AccessibilityServiceInfo info = automation.getServiceInfo();
		if ((info.flags & AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS) == 0) {
			info.flags |=  AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
			automation.setServiceInfo(info);
		}
		List<AccessibilityWindowInfo> windows = automation.getWindows();
		LOG.trace("Observed {} windows in UiAutomation", windows.size());
		for (AccessibilityWindowInfo window : windows) {
			LOG.trace("Window: {}", window);
			if (window.getType() == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
				return true;
			}
		}
		return false;
	}

	@RequiresApi(UiAutomatorExtensions.UI_AUTOMATOR_VERSION)
	public static void rotateDevice() {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		int currentRotation = device.getDisplayRotation();
		UiAutomation automation = getInstrumentation().getUiAutomation();
		automation.setRotation(toRotationFreeze(toOppositeRotation(currentRotation)));
	}

	@RequiresApi(UiAutomatorExtensions.UI_AUTOMATOR_VERSION)
	public static void stopRotateDevice() {
		UiAutomation automation = getInstrumentation().getUiAutomation();
		automation.setRotation(UiAutomation.ROTATION_UNFREEZE);
	}

	private static int toOppositeRotation(int rotation) {
		switch (rotation) {
			case Surface.ROTATION_0:
			case Surface.ROTATION_180:
				return Surface.ROTATION_90;
			case Surface.ROTATION_90:
			case Surface.ROTATION_270:
				return Surface.ROTATION_0;
			default:
				throw new IllegalArgumentException("Unknown rotation: " + rotation);
		}
	}

	/**
	 * @throws IllegalArgumentException if rotation is not one of the Surface rotation values.
	 * @see UiDevice#getDisplayRotation()
	 * @see UiAutomation#setRotation(int) 
	 * @see Surface#ROTATION_0
	 * @see Surface#ROTATION_90
	 * @see Surface#ROTATION_180
	 * @see Surface#ROTATION_270
	 */
	@RequiresApi(UiAutomatorExtensions.UI_AUTOMATOR_VERSION)
	public static int toRotationFreeze(int rotation) {
		switch (rotation) {
			case Surface.ROTATION_0:
				return UiAutomation.ROTATION_FREEZE_0;
			case Surface.ROTATION_90:
				return UiAutomation.ROTATION_FREEZE_90;
			case Surface.ROTATION_180:
				return UiAutomation.ROTATION_FREEZE_180;
			case Surface.ROTATION_270:
				return UiAutomation.ROTATION_FREEZE_270;
			default:
				throw new IllegalArgumentException("Unknown rotation: " + rotation);
		}
	}
}
