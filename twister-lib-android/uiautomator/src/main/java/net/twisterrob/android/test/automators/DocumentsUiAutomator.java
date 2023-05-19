package net.twisterrob.android.test.automators;

import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiScrollable;
import androidx.test.uiautomator.UiSelector;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import net.twisterrob.android.annotation.IdResName;

import static net.twisterrob.android.test.automators.UiAutomatorExtensions.UI_AUTOMATOR_VERSION;

/**
 * Documents UI ({@code com.android.documentsui}) is a System App,
 * so source code is available in AOSP:
 * <ul>
 *     <li>Before Oreo it was located in {@code platform/frameworks/base/packages/DocumentsUI}
 *         <a href="https://github.com/aosp-mirror/platform_frameworks_base/tree/nougat-release/packages/DocumentsUI">Nougat</a>
 *     </li>
 *     <li>In Oreo it was relocated to {@code platform/packages/apps/DocumentsUI}
 *         <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/oreo-release">Oreo</a>
 *     </li>
 *     <li>Latest release in 2023 May:
 *         <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/android13-release">Tiramisu</a>
 *     </li>
 * </ul>
 */
public class DocumentsUiAutomator {
	public static final String PACKAGE_DOCUMENTS_UI =
			Build.VERSION_CODES.TIRAMISU <= Build.VERSION.SDK_INT
					? "com.google.android.documentsui"
					: "com.android.documentsui";

	/**
	 * Toolbar of the activity.
	 *
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/android13-release/res/layout/drawer_layout.xml#85">Source</a>
	 */
	public static @IdResName String toolbar() {
		return UiAutomatorExtensions.externalId(PACKAGE_DOCUMENTS_UI, "toolbar");
	}

	/**
	 * Up navigation hamburger menu that opens the drawer.
	 */
	public static String showRoots() throws PackageManager.NameNotFoundException {
		return UiAutomatorExtensions.externalString(PACKAGE_DOCUMENTS_UI, "drawer_open", "Show roots");
	}
	/**
	 * Up navigation hamburger menu that closes the drawer.
	 */
	public static String hideRoots() throws PackageManager.NameNotFoundException {
		return UiAutomatorExtensions.externalString(PACKAGE_DOCUMENTS_UI, "drawer_close", "Hide roots");
	}

	/**
	 * Toolbar of the navigation drawer.
	 *
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/android13-release/res/layout/drawer_layout.xml#85">Source</a>
	 */
	public static @IdResName String drawerToolbar() {
		return UiAutomatorExtensions.externalId(PACKAGE_DOCUMENTS_UI, "roots_toolbar");
	}

	/**
	 * Title of the navigation drawer for {@link android.content.Intent#ACTION_OPEN_DOCUMENT}.
	 *
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/android13-release/res/values/strings.xml#38">Source</a>
	 * @see #getDrawerTitle()
	 */
	public static String drawerTitleOpenFrom() throws PackageManager.NameNotFoundException {
		return UiAutomatorExtensions.externalString(PACKAGE_DOCUMENTS_UI, "title_open", "Open from");
	}

	/**
	 * Title of the navigation drawer for {@link android.content.Intent#ACTION_CREATE_DOCUMENT}.
	 *
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/android13-release/res/values/strings.xml#40">Source</a>
	 * @see #getDrawerTitle()
	 */
	public static String drawerTitleSaveTo() throws PackageManager.NameNotFoundException {
		return UiAutomatorExtensions.externalString(PACKAGE_DOCUMENTS_UI, "title_save", "Save to");
	}

	/**
	 * List of roots in the navigation drawer.
	 *
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/android13-release/res/layout/fragment_roots.xml#18">Source</a>
	 */
	public static @IdResName String drawerRootsList() {
		if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
			return UiAutomatorExtensions.externalId(PACKAGE_DOCUMENTS_UI, "roots_list");
		} else {
			return UiAutomatorExtensions.androidId(android.R.id.list);
		}
	}

	public static @IdResName String drawerItemName() {
		return UiAutomatorExtensions.androidId(android.R.id.title);
	}

	/**
	 * List of directories and files in picker.
	 *
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/android13-release/res/layout/fragment_directory.xml#42">Source</a>
	 */
	public static @IdResName String documentsList() {
		if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
			return UiAutomatorExtensions.externalId(PACKAGE_DOCUMENTS_UI, "dir_list");
		} else {
			return UiAutomatorExtensions.externalId(PACKAGE_DOCUMENTS_UI, "list");
		}
	}

	/**
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/android13-release/res/layout/item_doc_grid.xml#160">Source (Grid)</a>
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/android13-release/res/layout/item_doc_list.xml#104">Source (List)</a>
	 */
	public static @IdResName String documentTitle() {
		return UiAutomatorExtensions.androidId(android.R.id.title);
	}

	public static @IdResName String save() {
		return UiAutomatorExtensions.androidId(android.R.id.button1);
	}

	/**
	 * Action mode's OPEN button on the toolbar. Newer versions have SELECT.
	 *
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/android13-release/res/menu/action_mode_menu.xml#38">Source (33/T)</a>
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/pie-release/res/menu/action_mode_menu.xml#19">Source (28/P)</a>
	 * @see <a href="https://android.googlesource.com/platform/packages/apps/DocumentsUI/+/refs/heads/oreo-release/res/menu/file_context_menu.xml#26">Source (26/O)</a>
	 */
	public static @IdResName String open() {
		if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
			return UiAutomatorExtensions.externalId(PACKAGE_DOCUMENTS_UI, "action_menu_select");
		} else if (Build.VERSION_CODES.O_MR1 <= Build.VERSION.SDK_INT) {
			return UiAutomatorExtensions.externalId(PACKAGE_DOCUMENTS_UI, "action_menu_open");
		} else {
			return UiAutomatorExtensions.externalId(PACKAGE_DOCUMENTS_UI, "menu_open");
		}
	}

	public static UiObject selectRootInDrawer(@NonNull String rootName)
			throws UiObjectNotFoundException {
		UiScrollable list = new UiScrollable(new UiSelector().resourceId(drawerRootsList()));
		return list.getChildByText(new UiSelector().resourceId(drawerItemName()), rootName);
	}

	/**
	 * Try to get the toolbar text for the picker activity's drawer.
	 *
	 * @see #drawerToolbar()
	 * @see #drawerTitleOpenFrom()
	 * @see #drawerTitleSaveTo()
	 */
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static String getDrawerTitle() throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiSelector toolbar = new UiSelector().resourceId(drawerToolbar());
		UiSelector firstTextView = new UiSelector().className(TextView.class);
		UiObject object = device.findObject(toolbar.childSelector(firstTextView));
		return object.getText();
	}

	/**
	 * Try to get the title text for the picker activity.
	 */
	@RequiresApi(UI_AUTOMATOR_VERSION)
	public static String getActivityTitle() throws UiObjectNotFoundException {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiSelector toolbar = new UiSelector().resourceId(toolbar());
		UiSelector firstTextView = new UiSelector().className(TextView.class);
		UiObject object = device.findObject(toolbar.childSelector(firstTextView));
		return object.getText();
	}
	public static UiObject getUpNavigation() {
		UiDevice device = UiDevice.getInstance(getInstrumentation());
		UiSelector toolbar = new UiSelector().resourceId(toolbar());
		UiSelector firstImageButton = new UiSelector().className(ImageButton.class);
		return device.findObject(toolbar.childSelector(firstImageButton));
	}
	public static UiObject selectItemInList(String name) throws UiObjectNotFoundException {
		UiScrollable list = new UiScrollable(new UiSelector().resourceId(documentsList()));
		return list.getChildByText(new UiSelector().resourceId(documentTitle()), name);
	}
}
