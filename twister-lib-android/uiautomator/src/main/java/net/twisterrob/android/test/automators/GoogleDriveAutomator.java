package net.twisterrob.android.test.automators;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.test.uiautomator.*;

import static androidx.test.platform.app.InstrumentationRegistry.*;

import net.twisterrob.android.annotation.IdResName;

import static net.twisterrob.android.test.automators.UiAutomatorExtensions.UI_AUTOMATOR_VERSION;

/**
 * Resource names last updated from {@code com.google.android.apps.docs}
 * version 213141016 (2.23.161.1.all.alldpi) that was installed on my Google Pixel 7 Pro (Android 13).
 * The UI significantly changed, but still working on 2.23.251.0.all.alldpi on API 28 Store emulator.
 * TODO extract version detection and UI class(es) based on {@link PackageInfo#versionCode}.
 */
public class GoogleDriveAutomator {
	public static final String PACKAGE_GOOGLE_DRIVE = "com.google.android.apps.docs";
	public static final String PACKAGE_GOOGLE_SIGN_IN = "com.google.android.gms";

	public static @IdResName String newFolderTitle() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "edit_text");
	}

	/**
	 * Container of hamburger, title, search of Drive home activity.
	 */
	public static @IdResName String toolbar() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "toolbar");
	}
	public static @IdResName String dialogTitle() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "title");
	}
	public static @IdResName String folderTitleInList() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "entry_label");
	}
	public static @IdResName String folderList() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "doclist_recycler_view");
	}

	/**
	 * Up-navigation button in the folder selector, which is a popup activity.
	 */
	public static @IdResName String up() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "up_affordance");
	}
	public static @IdResName String saveToDriveFileName() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "upload_title_edittext");
	}

	/**
	 * Destination folder spinner in the "Save to Drive" dialog.
	 */
	public static @IdResName String uploadFolder() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "upload_folder_autocomplete");
	}
	public static @IdResName String newFolder() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "create_folder");
	}
	public static @IdResName String confirmCreateFolder() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "positive_button");
	}

	public static @IdResName String searchBar() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "open_search_bar");
	}

	public static @IdResName String bottomNavigation() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "bottom_navigation");
	}

	public static @IdResName String welcomeScreen() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "welcome");
	}
	public static @IdResName String skipWelcome() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "skip");
	}

	/**
	 * Label of the home activity of Drive in default startup state.
	 */
	public static String myDrive() throws NameNotFoundException {
		return UiAutomatorExtensions.externalString(PACKAGE_GOOGLE_DRIVE, "menu_my_drive", "My Drive");
	}

	/**
	 * Title of the dialog that pops up to confirm drive upload from share intent.
	 */
	public static String saveToDriveDialogTitle() throws NameNotFoundException {
		return UiAutomatorExtensions.externalString(PACKAGE_GOOGLE_DRIVE, "upload_shared_item_title", "Save to Drive");
	}

	/**
	 * Title of the item in the system chooser to select uploading to Drive.
	 */
	public static String saveToDriveChooserTitle() throws NameNotFoundException {
		if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
			// TODO this doesn't really depend on API level, but probably the version of Drive installed.
			return UiAutomatorExtensions.externalString(PACKAGE_GOOGLE_DRIVE, "app_name_drive", "Drive");
		} else {
			return UiAutomatorExtensions.externalString(PACKAGE_GOOGLE_DRIVE, "upload_shared_item_title", "Save to Drive");
		}
	}

	/**
	 * Title of the dialog that pops up to when we're cancelling upload.
	 */
	public static @IdResName String alertTitle() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "alertTitle");
	}

	public static String cancelUploadDialogTitle() throws NameNotFoundException {
		return UiAutomatorExtensions.externalString(PACKAGE_GOOGLE_DRIVE, "cancel_dialog_title", "Cancel upload?");
	}

	/**
	 * Positive confirmation button of the folder selector, which pops up from Save to Drive dialog.
	 */
	public static @IdResName String selectFolder() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "positive_button");
	}

	/**
	 * Positive confirmation button of {@link #saveToDriveDialogTitle()}.
	 */
	public static @IdResName String saveToDriveDialogSave() {
		return UiAutomatorExtensions.externalId(PACKAGE_GOOGLE_DRIVE, "save_button");
	}

	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static UiObject selectTitleInList(String folderName) throws UiObjectNotFoundException {
		UiScrollable list = new UiScrollable(new UiSelector().resourceId(folderList()));
		return list.getChildByText(new UiSelector().resourceId(folderTitleInList()), folderName);
	}

	/**
	 * Try to get the text for the home activity of Drive.
	 */
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static String getActivityTitle() throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiSelector toolbar = new UiSelector().resourceId(toolbar());
		UiSelector firstTextView = new UiSelector().className(TextView.class);
		UiObject object = device.findObject(toolbar.childSelector(firstTextView));
		return object.getText();
	}

	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static String getAlertTitle() throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiObject object = device.findObject(new UiSelector().resourceId(alertTitle()));
		return object.getText();
	}

	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static UiObject getUpNavigation() {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiSelector toolbar = new UiSelector().resourceId(toolbar());
		UiSelector firstImageButton = new UiSelector().className(ImageButton.class);
		return device.findObject(toolbar.childSelector(firstImageButton));
	}
}
