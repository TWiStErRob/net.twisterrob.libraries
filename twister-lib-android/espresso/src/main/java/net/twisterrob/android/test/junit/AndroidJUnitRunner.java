package net.twisterrob.android.test.junit;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.view.View;

import static android.content.pm.PackageManager.GET_META_DATA;
import static android.content.pm.PackageManager.NameNotFoundException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.espresso.base.BaseLayerModule;
import androidx.test.espresso.base.DefaultFailureHandler;
import androidx.test.internal.runner.RunnerArgs;

import net.twisterrob.android.test.junit.internal.DexPathListReflection;
import net.twisterrob.java.exceptions.StackTrace;
import net.twisterrob.java.utils.ReflectionTools;
import net.twisterrob.java.utils.StringTools;

public class AndroidJUnitRunner extends androidx.test.runner.AndroidJUnitRunner {
	private static final Logger LOG = LoggerFactory.getLogger(AndroidJUnitRunner.class);
	/**
	 * @see RunnerArgs#ARGUMENT_NOT_TEST_PACKAGE
	 * @type java.lang.String
	 * @format {@code pack.age1,pack.age2,pack.age3}
	 */
	@SuppressWarnings("JavaDoc")
	private static final String DEFAULT_EXCLUDED_PACKAGES = "defaultExcludedPackages";
	// copied from androidx.test.internal.runner.RunnerArgs
	private static final String ARGUMENT_CLASSPATHTOSCAN = "classpathToScan";
	private static final String ARGUMENT_TEST_CLASS = "class";
	private static final String ARGUMENT_NOT_TEST_CLASS = "notClass";
	private static final String ARGUMENT_NOT_TEST_PACKAGE = "notPackage";
	private static final String ARGUMENT_DISABLE_ANALYTICS = "disableAnalytics";
	private static final String CLASS_NAME = "([A-Z][A-Za-z0-9_$]*)";
	private static final Pattern INNER_CLASS =
			Pattern.compile("((?:[a-z0-9_]+\\.)*)" + CLASS_NAME + "\\." + CLASS_NAME + "");
	private static final String INNER_CLASS_FIX = "$1$2\\$$3";

	@Override public void onCreate(Bundle arguments) {
		Bundle metaData = getInstrumentationMetaData();
		setDefaults(arguments, metaData != null? metaData : Bundle.EMPTY);
		// specifyDexMakerCacheProperty is unconditionally called, but behavior was observed in Android 5.0)
		ThreadPolicy originalPolicy = StrictMode.allowThreadDiskWrites();
		try {
			super.onCreate(arguments);
		} finally {
			StrictMode.setThreadPolicy(originalPolicy);
		}
		/**
		 * @see BaseLayerModule#provideFailureHander
		 */
		DefaultFailureHandler defaultHandler = new DefaultFailureHandler(getTargetContext());
		Espresso.setFailureHandler(new DetailedFailureHandler(defaultHandler));
	}

	private @Nullable Bundle getInstrumentationMetaData() {
		try {
			PackageManager pm = getContext().getPackageManager();
			ComponentName instrumentationRunner = new ComponentName(getContext(), getClass());
			// InstrumentationInfo.metaData can be null, for example when not specifying a test:
			// adb shell am instrument -w -r -e debug false <test-package>/....AndroidJUnitRunner
			return pm.getInstrumentationInfo(instrumentationRunner, GET_META_DATA).metaData;
		} catch (NameNotFoundException ex) {
			LOG.warn("Cannot find own instrumentation info, wonder how do I exists then?", ex);
			return null;
		}
	}

	@Override public void onStart() {
		try {
			// JavaScriptBridge does KitKat check, but not sure when WebView ctor is fixed, let's see when else it fails
			wrapRunOnMainStrict = VERSION.SDK_INT <= VERSION_CODES.GINGERBREAD_MR1;
			super.onStart();
		} finally {
			wrapRunOnMainStrict = false;
		}
	}
	private boolean wrapRunOnMainStrict = false;
	@Override public void runOnMainSync(final Runnable runner) {
		if (!wrapRunOnMainStrict) {
			super.runOnMainSync(runner);
		} else {
			super.runOnMainSync(new Runnable() {
				@Override public void run() {
					ThreadPolicy originalPolicy = StrictMode.allowThreadDiskWrites();
					try {
						runner.run();
					} finally {
						StrictMode.setThreadPolicy(originalPolicy);
					}
				}
			});
		}
	}

	private void setDefaults(@NonNull Bundle arguments, @NonNull Bundle manifest) {
		if (!arguments.containsKey(ARGUMENT_DISABLE_ANALYTICS)) {
			// Tried to put it in manifest, but it failed with cannot cast Boolean to String in super.onCreate
			arguments.putString(ARGUMENT_DISABLE_ANALYTICS, Boolean.TRUE.toString());
		}
		if (!arguments.containsKey(ARGUMENT_TEST_CLASS)) { // @see TestRequestBuilder#validate
			String packages = StringTools.join(Arrays.asList(
					"java",
					"javax",
					"android",
					"com.google",
					"net.twisterrob.android.test",
					"net.twisterrob.test"
			), ",");
			if (arguments.containsKey(ARGUMENT_NOT_TEST_PACKAGE)) {
				packages += "," + arguments.getString(ARGUMENT_NOT_TEST_PACKAGE);
			}
			if (manifest.containsKey(DEFAULT_EXCLUDED_PACKAGES)) {
				packages += "," + manifest.getString(DEFAULT_EXCLUDED_PACKAGES);
			}
			arguments.putString(ARGUMENT_NOT_TEST_PACKAGE, packages);
		}
		if (arguments.containsKey(ARGUMENT_TEST_CLASS)) {
			String newArgumentValue = fixInnerClasses(arguments.getString(ARGUMENT_TEST_CLASS));
			arguments.putString(ARGUMENT_TEST_CLASS, newArgumentValue);
		}
		if (arguments.containsKey(ARGUMENT_NOT_TEST_CLASS)) {
			String newArgumentValue = fixInnerClasses(arguments.getString(ARGUMENT_NOT_TEST_CLASS));
			arguments.putString(ARGUMENT_NOT_TEST_CLASS, newArgumentValue);
		}
		if (!arguments.containsKey(ARGUMENT_CLASSPATHTOSCAN)) {
			// Make sure we scan all dex files for tests.
			List<File> classpath = new DexPathListReflection(this).dexClasspath();
			if (!classpath.isEmpty()) {
				arguments.putString(ARGUMENT_CLASSPATHTOSCAN, StringTools.join(classpath, ":"));
			}
		}
	}

	/**
	 * IDEA creates test runs with {@code pack.age.Outer.Inner} style,
	 * but {@link Class#forName(String)} (used by  requires {@code pack.age.Outer$Inner}.
	 * This method bridges the gap, before {@link androidx.test.runner.AndroidJUnitRunner} sees the arguments.
	 * @param classes contains class names separated by something other than dots and dollars (e.g. comma)
	 * @return all occurrences of {@code Inner.Outer} class names are now {@code Inner$Outer} (even multiple nested)
	 * @see androidx.test.internal.runner.TestRequestBuilder#loadClasses
	 * @see androidx.test.internal.runner.TestLoader#doLoadClass
	 */
	private static String fixInnerClasses(String classes) {
		Matcher matcher = INNER_CLASS.matcher(classes);
		String fixed = classes;
		while (matcher.find()) {
			fixed = matcher.replaceAll(INNER_CLASS_FIX);
			matcher.reset(fixed);
		}
		return fixed;
	}

	static class DetailedFailureHandler implements FailureHandler {

		private final @NonNull FailureHandler defaultFailureHandler;

		DetailedFailureHandler(@NonNull FailureHandler handler) {
			defaultFailureHandler = handler;
		}

		@Override public void handle(Throwable error, org.hamcrest.Matcher<View> viewMatcher) {
			Throwable transformed = transformSpecialCases(error);
			defaultFailureHandler.handle(transformed, viewMatcher);
		}

		private Throwable addInitialCallStack(Throwable error) {
			Throwable cause = error;
			while (cause.getCause() != null) {
				cause = cause.getCause();
			}
			// We have to use reflection, because when a NoMatchingViewException is created with
			// a Builder, the builder.cause is passed to the parent class, even if it's null.
			// Do it for every exception because there might be other similar ones.
			ReflectionTools.enableInitCause(cause);
			cause.initCause(new StackTrace("View interaction was initiated here"));
			return error;
		}

		private Throwable transformSpecialCases(Throwable originalError) {
			Throwable transformed = originalError;
			transformed = transformToPreserveStackTrace(transformed);
			// if an onView().check(...) ViewAssertion throws an exception the stack trace is (simplified):
			// at ....SomeCustom$ViewAssertion.check()
			// at androidx.test.espresso.ViewInteraction$2.call(ViewInteraction.java:282)
			// at android.os.Looper.loop(Looper.java:135)
			// at android.app.ActivityThread.main(ActivityThread.java:5221)
			// which means without this transformation it's impossible to see where the check() was initiated from
			// the only thing visible is the utility class that is the assertion implementation.
			// This works because the exception is thrown back to the test running thread in:
			// androidx.test.espresso.ViewInteraction.waitForAndHandleInteractionResults.
			// TODEL This could be obsolete based on current state of DefaultHandler, test it.
			// example: write a custom ViewAssertion inline and in override check() throw a RuntimeException
			// TODO when is this needed again? see above, and test!
			transformed = addInitialCallStack(transformed);
			return transformed;
		}

		private Throwable transformToPreserveStackTrace(Throwable error) {
			// TODO generalize based on conditions in DefaultFailureHandler
			if (error instanceof NoActivityResumedException) {
				// Wrap it in the same type, using the same message to create the illusion of nothing happened.
				// DefaultFailureHandler will re-set the stacktrace in getUserFriendlyError, but we keep the original.
				// An example of this is Espresso.pressBack() throwing "Pressed back and killed the app",
				// but the origin shows up as waitForAndHandleInteractionResults
				// instead of waitForPendingForegroundActivities.
				return new NoActivityResumedException(error.getMessage(), error);
			}
			return error;
		}
	}
}
/* onCreate StrictMode: read needed because of this (Android 5.0):
StrictMode policy violation; ~duration=122 ms: android.os.StrictMode$StrictModeDiskReadViolation: policy=2815 violation=2
	at android.os.StrictMode$AndroidBlockGuardPolicy.onReadFromDisk(StrictMode.java:1137)
	at libcore.io.BlockGuardOs.access(BlockGuardOs.java:67)
	at java.io.File.doAccess(File.java:283)
	at java.io.File.exists(File.java:363)
	at android.app.ContextImpl.getDir(ContextImpl.java:2668)
	at androidx.test.runner.MonitoringInstrumentation.specifyDexMakerCacheProperty(MonitoringInstrumentation.java:187)
	at androidx.test.runner.MonitoringInstrumentation.onCreate(MonitoringInstrumentation.java:152)
	at androidx.test.runner.AndroidJUnitRunner.onCreate(AndroidJUnitRunner.java:209)
*/
/* onCreate StrictMode: write needed because of this (Android 5.0):
StrictMode policy violation; ~duration=79 ms: android.os.StrictMode$StrictModeDiskWriteViolation: policy=2813 violation=1
	at android.os.StrictMode$AndroidBlockGuardPolicy.onWriteToDisk(StrictMode.java:1111)
	at libcore.io.BlockGuardOs.mkdir(BlockGuardOs.java:172)
	at java.io.File.mkdirErrno(File.java:874)
	at java.io.File.mkdir(File.java:865)
	at android.app.ContextImpl.getDir(ContextImpl.java:2669)
	at androidx.test.runner.MonitoringInstrumentation.specifyDexMakerCacheProperty(MonitoringInstrumentation.java:187)
	at androidx.test.runner.MonitoringInstrumentation.onCreate(MonitoringInstrumentation.java:152)
	at androidx.test.runner.AndroidJUnitRunner.onCreate(AndroidJUnitRunner.java:209)
 */
/* runOnMainSync(JavaScriptBridge.install) StrictMode: read needed because of this (2.3.7):
StrictMode policy violation; ~duration=114 ms: android.os.StrictMode$StrictModeDiskReadViolation: policy=247 violation=2
	at android.os.StrictMode$AndroidBlockGuardPolicy.onReadFromDisk(StrictMode.java:745)
	at android.database.sqlite.SQLiteStatement.simpleQueryForLong(SQLiteStatement.java:106)
	at android.database.sqlite.SQLiteDatabase.getVersion(SQLiteDatabase.java:928)
	at android.webkit.WebViewDatabase.getInstance(WebViewDatabase.java:196)
	at android.webkit.WebView.<init>(WebView.java:981)
	at android.webkit.WebView.<init>(WebView.java:958)
	at android.webkit.WebView.<init>(WebView.java:948)
	at android.webkit.WebView.<init>(WebView.java:939)
	at android.support.test.espresso.web.bridge.AndroidJavaScriptBridgeInstaller.install(AndroidJavaScriptBridgeInstaller.java:76)
	at android.support.test.espresso.web.bridge.JavaScriptBridge.installBridge(JavaScriptBridge.java:76)
	at java.lang.reflect.Method.invoke(Method.java:507)
	at android.support.test.runner.MonitoringInstrumentation.tryLoadingJsBridge(MonitoringInstrumentation.java:622)
	at android.support.test.runner.MonitoringInstrumentation$4.run(MonitoringInstrumentation.java:230)
	at android.app.Instrumentation$SyncRunnable.run(Instrumentation.java:1466)
	at android.os.Handler.handleCallback(Handler.java:587)
	at android.os.Handler.dispatchMessage(Handler.java:92)
	at android.os.Looper.loop(Looper.java:130)
	at android.app.ActivityThread.main(ActivityThread.java:3683)
Schedule to run on Main thread at:
	at android.app.Instrumentation.runOnMainSync(Instrumentation.java:350)
	at android.support.test.runner.MonitoringInstrumentation.onStart(MonitoringInstrumentation.java:227)
	at android.support.test.runner.AndroidJUnitRunner.onStart(AndroidJUnitRunner.java:246)
	at net.twisterrob.android.test.junit.AndroidJUnitRunner.onStart(AndroidJUnitRunner.java:63)
	at android.app.Instrumentation$InstrumentationThread.run(Instrumentation.java:1448)
*/
/* runOnMainSync(JavaScriptBridge.install) StrictMode: write needed because of this (2.3.7):
StrictMode policy violation; ~duration=1 ms: android.os.StrictMode$StrictModeDiskWriteViolation: policy=245 violation=1
	at android.os.StrictMode$AndroidBlockGuardPolicy.onWriteToDisk(StrictMode.java:732)
	at android.database.sqlite.SQLiteDatabase.execSQL(SQLiteDatabase.java:1755)
	at android.webkit.WebViewDatabase.getInstance(WebViewDatabase.java:243)
	at android.webkit.WebView.<init>(WebView.java:981)
	... rest is the same as above
 */
