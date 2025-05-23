package net.twisterrob.android.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ActionMode.Callback;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;

import net.twisterrob.android.utils.log.LoggingDebugProvider.LoggingHelper;
import net.twisterrob.android.utils.tools.StringerTools;
import net.twisterrob.java.annotations.DebugHelper;

@DebugHelper
@SuppressLint("Registered") // allow registration if wanted without needing to subclass
public class LoggingActivity extends AppCompatActivity {
	private static final Logger LOG = LoggerFactory.getLogger("Activity");

	@SuppressWarnings("unused") // assign in child constructor for debugging something specific
	protected LoggingDebugProvider debugInfoProvider;
	@SuppressWarnings("unused") // assign in child constructor for debugging something specific
	protected final boolean logOnCreateView = globalLogOnCreateView;
	private static boolean globalLogOnCreateView = false;

	@SuppressWarnings("unused")
	public static void setGlobalLogOnCreateView(boolean log) {
		globalLogOnCreateView = log;
	}

	public LoggingActivity() {
		super();
		log("ctor");
	}

	@ContentView
	public LoggingActivity(@LayoutRes int contentLayoutId) {
		super(contentLayoutId);
		log("ctor", contentLayoutId);
	}

	//region Startup

	@Override public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		log("onCreate", savedInstanceState, persistentState);
		super.onCreate(savedInstanceState, persistentState);
	}
	@Override protected void onCreate(Bundle savedInstanceState) {
		log("onCreate", savedInstanceState);
		super.onCreate(savedInstanceState);
		LOG.trace("{}.loaderManager={}", getName(), LoaderManager.getInstance(this));
	}

	@Override public void onContentChanged() {
		log("onContentChanged");
		super.onContentChanged();
	}
	@Deprecated @SuppressWarnings("deprecation")
	@Override public void onSupportContentChanged() {
		log("onSupportContentChanged");
		super.onSupportContentChanged();
	}
	@Override public @Nullable View onCreateView(
			@Nullable View parent,
			@NonNull String name,
			@NonNull Context context,
			@NonNull AttributeSet attrs
	) {
		if (logOnCreateView) {
			log("onCreateView", parent, name, context, attrs);
		}
		return super.onCreateView(parent, name, context, attrs);
	}
	@Override public @Nullable View onCreateView(
			@NonNull String name,
			@NonNull Context context,
			@NonNull AttributeSet attrs
	) {
		if (logOnCreateView) {
			log("onCreateView", name, context, attrs);
		}
		return super.onCreateView(name, context, attrs);
	}

	@Deprecated @SuppressWarnings("deprecation")
	@Override public void onAttachFragment(android.app.Fragment fragment) {
		log("onAttachFragment", fragment);
		super.onAttachFragment(fragment);
	}
	@Deprecated @SuppressWarnings("deprecation")
	@Override public void onAttachFragment(@NonNull Fragment fragment) {
		log("onAttachFragment", fragment);
		super.onAttachFragment(fragment);
	}

	@Override protected void onRestart() {
		log("onRestart");
		super.onRestart();
	}

	@Override protected void onStart() {
		log("onStart");
		super.onStart();
	}

	@Override public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
		log("onRestoreInstanceState", savedInstanceState, persistentState);
		super.onRestoreInstanceState(savedInstanceState, persistentState);
	}
	@Override protected void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
		log("onRestoreInstanceState", savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
		log("onPostCreate", savedInstanceState, persistentState);
		super.onPostCreate(savedInstanceState, persistentState);
	}
	@Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		log("onPostCreate", savedInstanceState);
		super.onPostCreate(savedInstanceState);
	}

	@Override protected void onResume() {
		log("onResume");
		super.onResume();
	}
	@Override protected void onResumeFragments() {
		log("onResumeFragments");
		super.onResumeFragments();
	}
	@Override protected void onPostResume() {
		log("onPostResume");
		super.onPostResume();
	}

	@Override public void onAttachedToWindow() {
		log("onAttachedToWindow");
		super.onAttachedToWindow();
	}

	@Override public void onUserInteraction() {
		log("onUserInteraction");
		super.onUserInteraction();
	}

	//endregion Startup

	// activity is running

	//region Shutdown

	@Override protected void onUserLeaveHint() {
		log("onUserLeaveHint");
		super.onUserLeaveHint();
	}

	@Override protected void onPause() {
		log("onPause");
		super.onPause();
	}

	@Override public void onSaveInstanceState(@Nullable Bundle outState, @Nullable PersistableBundle outPersistentState) {
		log("onSaveInstanceState", outState, outPersistentState);
		super.onSaveInstanceState(outState, outPersistentState);
	}
	@Override protected void onSaveInstanceState(@NonNull Bundle outState) {
		log("onSaveInstanceState", outState);
		super.onSaveInstanceState(outState);
	}

	@Override protected void onStop() {
		log("onStop");
		super.onStop();
	}

	@Override public void onDetachedFromWindow() {
		log("onDetachedFromWindow");
		super.onDetachedFromWindow();
	}

	@Override protected void onDestroy() {
		log("onDestroy");
		super.onDestroy();
	}

	//endregion

	//region OptionsMenu

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		log("onCreateOptionsMenu", menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override public boolean onPrepareOptionsMenu(Menu menu) {
		log("onPrepareOptionsMenu", menu);
		return super.onPrepareOptionsMenu(menu);
	}
	@Override public void openOptionsMenu() {
		log("openOptionsMenu");
		super.openOptionsMenu();
	}
	@Override public boolean onMenuOpened(int featureId, Menu menu) {
		log("onMenuOpened", StringerTools.toFeatureString(featureId), menu);
		return super.onMenuOpened(featureId, menu);
	}
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		log("onOptionsItemSelected", item);
		return super.onOptionsItemSelected(item);
	}
	@Override public void closeOptionsMenu() {
		log("closeOptionsMenu");
		super.closeOptionsMenu();
	}
	@Override public void onOptionsMenuClosed(Menu menu) {
		log("onOptionsMenuClosed", menu);
		super.onOptionsMenuClosed(menu);
	}
	@Override public void invalidateOptionsMenu() {
		log("invalidateOptionsMenu");
		super.invalidateOptionsMenu();
	}
	@Override public void supportInvalidateOptionsMenu() {
		log("supportInvalidateOptionsMenu");
		super.supportInvalidateOptionsMenu();
	}

	//endregion OptionsMenu

	//region ActionMode

	@Override public void onSupportActionModeStarted(@NonNull ActionMode mode) {
		log("onSupportActionModeStarted", mode);
		super.onSupportActionModeStarted(mode);
	}
	@Override public void onSupportActionModeFinished(@NonNull ActionMode mode) {
		log("onSupportActionModeFinished", mode);
		super.onSupportActionModeFinished(mode);
	}
	@Override public ActionMode startSupportActionMode(@NonNull Callback callback) {
		log("startSupportActionMode", callback);
		return super.startSupportActionMode(callback);
	}
	@Nullable @Override public android.view.ActionMode onWindowStartingActionMode(
			android.view.ActionMode.Callback callback) {
		log("onWindowStartingActionMode", callback);
		return super.onWindowStartingActionMode(callback);
	}
	@Override public void onActionModeStarted(android.view.ActionMode mode) {
		log("onActionModeStarted", mode);
		super.onActionModeStarted(mode);
	}
	@Override public void onActionModeFinished(android.view.ActionMode mode) {
		log("onActionModeFinished", mode);
		super.onActionModeFinished(mode);
	}

	//endregion ActionMode

	//region OptionsPanel

	@Override public View onCreatePanelView(int featureId) {
		log("onCreatePanelView", StringerTools.toFeatureString(featureId));
		return super.onCreatePanelView(featureId);
	}
	@Override public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
		log("onCreatePanelMenu", StringerTools.toFeatureString(featureId), menu);
		return super.onCreatePanelMenu(featureId, menu);
	}
	@Override public boolean onPreparePanel(int featureId, View view, @NonNull Menu menu) {
		log("onPreparePanel", StringerTools.toFeatureString(featureId), view, menu);
		return super.onPreparePanel(featureId, view, menu);
	}
	@Override public void onPanelClosed(int featureId, @NonNull Menu menu) {
		log("onPanelClosed", StringerTools.toFeatureString(featureId), menu);
		super.onPanelClosed(featureId, menu);
	}

	//endregion OptionsPanel

	//region Up navigation

	@Override public boolean onNavigateUp() {
		log("onNavigateUp");
		return super.onNavigateUp();
	}
	@Override public boolean onSupportNavigateUp() {
		log("onSupportNavigateUp");
		return super.onSupportNavigateUp();
	}
	@Override public boolean navigateUpTo(@NonNull Intent upIntent) {
		log("navigateUpTo", upIntent);
		return super.navigateUpTo(upIntent);
	}
	@Override public void supportNavigateUpTo(@NonNull Intent upIntent) {
		log("supportNavigateUpTo", upIntent);
		super.supportNavigateUpTo(upIntent);
	}
	@Deprecated @SuppressWarnings("deprecation")
	@Override public boolean onNavigateUpFromChild(Activity child) {
		log("onNavigateUpFromChild", child);
		return super.onNavigateUpFromChild(child);
	}
	@Deprecated @SuppressWarnings("deprecation")
	@Override public boolean navigateUpToFromChild(Activity child, @NonNull Intent upIntent) {
		log("onNavigateUpFromChild", child, upIntent);
		return super.navigateUpToFromChild(child, upIntent);
	}

	@Override public void onCreateNavigateUpTaskStack(@NonNull android.app.TaskStackBuilder builder) {
		log("onCreateNavigateUpTaskStack", builder);
		super.onCreateNavigateUpTaskStack(builder);
	}
	@Override public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
		log("onCreateSupportNavigateUpTaskStack", builder);
		super.onCreateSupportNavigateUpTaskStack(builder);
	}

	@Override public void onPrepareNavigateUpTaskStack(android.app.TaskStackBuilder builder) {
		log("onPrepareNavigateUpTaskStack", builder);
		super.onPrepareNavigateUpTaskStack(builder);
	}
	@Override public void onPrepareSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
		log("onPrepareSupportNavigateUpTaskStack", builder);
		super.onPrepareSupportNavigateUpTaskStack(builder);
	}

	//endregion Up navigation

	//region Activity Navigation

	@Override public void onBackPressed() {
		log("onBackPressed");
		super.onBackPressed();
	}
	@Override public boolean onSearchRequested() {
		log("onSearchRequested");
		return super.onSearchRequested();
	}

	@Deprecated @SuppressWarnings("deprecation")
	@Override public void startActivityForResult(Intent intent, int requestCode) {
		log("startActivityForResult", intent, requestCode);
		super.startActivityForResult(intent, requestCode);
	}
	@Override public void startActivityFromFragment(@NonNull Fragment fragment, @Nullable Intent intent, int requestCode) {
		log("startActivityFromFragment", fragment, intent, requestCode);
		super.startActivityFromFragment(fragment, intent, requestCode);
	}

	@Override public void onActivityReenter(int resultCode, Intent data) {
		log("onActivityReenter", resultCode, data);
		super.onActivityReenter(resultCode, data);
	}
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO figure out a generic way to toString ints
		String code = resultCode == Activity.RESULT_OK? "RESULT_OK"
				: resultCode == Activity.RESULT_CANCELED? "RESULT_CANCELLED"
						: "RESULT_" + resultCode;
		log("onActivityResult", String.valueOf(requestCode), code, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override protected void onNewIntent(Intent intent) {
		log("onNewIntent", intent);
		super.onNewIntent(intent);
	}
	@Deprecated @SuppressWarnings("deprecation")
	@Override public Object onRetainCustomNonConfigurationInstance() {
		log("onRetainCustomNonConfigurationInstance");
		return super.onRetainCustomNonConfigurationInstance();
	}
	@Override public void onConfigurationChanged(@NonNull Configuration newConfig) {
		log("onConfigurationChanged", newConfig, getResources().getConfiguration());
		super.onConfigurationChanged(newConfig);
	}

	//endregion Activity Navigation

	//region Context Menu
	@Override public void registerForContextMenu(@NonNull View view) {
		log("registerForContextMenu", view);
		super.registerForContextMenu(view);
	}
	@Override public void openContextMenu(@NonNull View view) {
		log("openContextMenu", view);
		super.openContextMenu(view);
	}

	@Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		log("onCreateContextMenu", menu, v, menuInfo);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override public boolean onContextItemSelected(MenuItem item) {
		log("onContextItemSelected", item);
		return super.onContextItemSelected(item);
	}

	@Override public void closeContextMenu() {
		log("closeContextMenu");
		super.closeContextMenu();
	}
	@Override public void onContextMenuClosed(Menu menu) {
		log("onContextMenuClosed", menu);
		super.onContextMenuClosed(menu);
	}

	@Override public void unregisterForContextMenu(@NonNull View view) {
		log("unregisterForContextMenu", view);
		super.unregisterForContextMenu(view);
	}

	//endregion

	@Override public void onLowMemory() {
		log("onLowMemory");
		super.onLowMemory();
	}
	@Override public void onTrimMemory(int level) {
		log("onTrimMemory", StringerTools.toTrimMemoryString(level));
		super.onTrimMemory(level);
	}

	@Override protected void onTitleChanged(CharSequence title, int color) {
		log("onTitleChanged", title, StringerTools.toColorString(color));
		super.onTitleChanged(title, color);
	}
	@Override protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
		log("onChildTitleChanged", childActivity, title);
		super.onChildTitleChanged(childActivity, title);
	}

	protected void log(@NonNull String name, @NonNull Object... args) {
		LoggingHelper.log(LOG, getName(), name, debugInfoProvider, args);
	}

	protected @NonNull String getName() {
		return getClass().getSimpleName() + "@" + StringerTools.hashString(this);
	}
}
