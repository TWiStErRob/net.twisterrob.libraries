package net.twisterrob.android.test.automators;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.slf4j.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.junit.MatcherAssume.*;
import static org.junit.Assert.*;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Build.*;

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
	public static void clickOn(@IdResName String id) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiObject object = device.findObject(new UiSelector().resourceId(id));
		assertTrue("expected to click and new window appear", object.clickAndWaitForNewWindow());
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void clickOnLabel(String label) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiObject object = device.findObject(new UiSelector().text(label));
		assertTrue("expected to click and new window appear", object.clickAndWaitForNewWindow());
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void shortClickOn(@IdResName String id) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiObject object = device.findObject(new UiSelector().resourceId(id));
		assertTrue("expected to click", object.click());
	}
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static void shortClickOnLabel(String label) throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiObject object = device.findObject(new UiSelector().text(label));
		assertTrue("expected to click", object.click());
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

	private static final List<Integer> FLAKY_BACK_VERSIONS = Arrays.asList(
			VERSION_CODES.KITKAT,
			VERSION_CODES.M,
			29
	);
	@RequiresApi(VERSION_CODES.JELLY_BEAN)
	public static void pressBackExternal() {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		if (FLAKY_BACK_VERSIONS.contains(VERSION.SDK_INT)
				&& "com.android.settings".equals(device.getCurrentPackageName())) {
			// net.twisterrob.inventory.android.activity.PreferencesActivityTest.testInfoSettings
			// fails because pressBack returns false even though the Settings is closed.
			device.pressBack();
		} else {
			assertTrue("expected to press Back button", device.pressBack());
		}
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
}
