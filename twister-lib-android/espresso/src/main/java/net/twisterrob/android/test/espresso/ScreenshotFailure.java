package net.twisterrob.android.test.espresso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.platform.io.OutputDirCalculator;
import androidx.test.runner.lifecycle.Stage;

import net.twisterrob.android.test.junit.InstrumentationExtensions;
import net.twisterrob.java.annotations.DebugHelper;
import net.twisterrob.java.io.IOTools;

public class ScreenshotFailure implements TestRule {
	private static final Logger LOG = LoggerFactory.getLogger(ScreenshotFailure.class);
	private final File targetDir;
	private final Instrumentation instrumentation;

	public ScreenshotFailure() {
		this(InstrumentationRegistry.getInstrumentation());
	}

	@SuppressLint("RestrictedApi")
	public ScreenshotFailure(@NonNull Instrumentation instrumentation) {
		this(instrumentation, new OutputDirCalculator().getOutputDir());
	}

	public ScreenshotFailure(@NonNull Instrumentation instrumentation, @NonNull File targetDir) {
		this.instrumentation = instrumentation;
		this.targetDir = targetDir;
	}

	@DebugHelper
	public static void captureNow(String description) {
		captureNow(Description.createTestDescription(ScreenshotFailure.class, description));
	}

	@DebugHelper
	public static void captureNow(Description description) {
		class FakeException extends Exception {
			private static final long serialVersionUID = 0;
		}
		try {
			new ScreenshotFailure().apply(new Statement() {
				@Override public void evaluate() throws Throwable {
					throw new FakeException();
				}
			}, description).evaluate();
		} catch (FakeException ignore) {
			// this is thrown only to trigger taking a screenshot
		} catch (Throwable ex) {
			throw new IllegalStateException("Cannot capture screen shot", ex);
		}
	}

	@Override public Statement apply(final Statement base, final Description description) {
		return new ScreenshotStatement(base, description);
	}

	private class ScreenshotStatement extends Statement {
		private final Statement base;
		private final Description description;

		public ScreenshotStatement(Statement base, Description description) {
			this.base = base;
			this.description = description;
		}

		@Override public void evaluate() throws Throwable {
			long started = System.currentTimeMillis();
			try {
				base.evaluate();
			} catch (Throwable ex) {
				if (ex instanceof AssumptionViolatedException) {
					// assumeThat(...) is not a failure, it's an ignore, so skip screenshot.
					LOG.debug("Screenshot not taken for {} in {}", ex.getClass(), description);
					throw ex;
				}
				try {
					String dirName = String.format(Locale.ROOT, "%s", description.getClassName());
					String shotName = String.format(Locale.ROOT, "%d_%s.png", started, description.getMethodName());
					File shot = takeScreenshot(fixFileSystemName(dirName), fixFileSystemName(shotName));
					LOG.warn("Screenshot taken to {}", getADBPullPath(shot));
				} catch (Throwable shotEx) {
					if (VERSION_CODES.KITKAT <= VERSION.SDK_INT) {
						ex.addSuppressed(shotEx);
					} else {
						throw new MultipleFailureException(Arrays.asList(ex, shotEx));
					}
				}
				throw ex;
			}
		}

		private @NonNull String fixFileSystemName(@NonNull String name) {
			return name.replaceAll("[^a-zA-Z0-9\\-_$!#@+ .,(){}\\[\\]]", "_");
		}
	}

	@SuppressLint("SdCardPath")
	private static File getADBPullPath(File shot) {
		if (shot == null) {
			return null;
		}
		return new File(shot.getAbsolutePath().replace("/storage/emulated/0/", "/sdcard/"));
	}

	/**
	 * Ignored <code>com.googlecode.eyesfree.utils.ScreenshotUtils.createScreenshot(getApplicationContext());</code>,
	 * because it requires {@code  android.permission.READ_FRAME_BUFFER} which is signature level.
	 */
	private File takeScreenshot(@NonNull String dirName, @NonNull String shotName) throws IOException {
		if (VERSION_CODES.JELLY_BEAN_MR1 <= VERSION.SDK_INT) {
			// androidx.test.uiautomator.UiDevice#takeScreenshot method says @since API 17, so it could be used.
			// But you can't actually acquire a UiDevice instance to call the method on API 17, because it throws:
			// java.lang.NoSuchMethodError: android.app.Instrumentation.getUiAutomation
			// androidx.test.uiautomator.UiDevice is from uiautomator
			// com.android.uiautomator.core.UiDevice would be from the old testing framework, but it's not available.
			//UiDevice.getInstance(instrumentation).takeScreenshot(target);
			//return target;
		}
		Bitmap shot = null;
		if (VERSION_CODES.JELLY_BEAN_MR2 <= VERSION.SDK_INT) {
			LOG.trace("Taking screenshot with UiAutomation");
			shot = instrumentation.getUiAutomation().takeScreenshot();
		}
		if (shot == null) {
			LOG.trace("Taking screenshot with drawing cache.");
			try {
				Activity activity = InstrumentationExtensions.getActivityInStage(Stage.RESUMED);
				shot = shootActivity(activity);
			} catch (Exception ex) {
				LOG.warn("No activity found to shoot.", ex);
				return null;
			}
		}
		if (shot != null) {
			File dir = new File(targetDir, dirName);
			File target = new File(dir, shotName);
			IOTools.ensure(dir);
			FileOutputStream stream = new FileOutputStream(target);
			try {
				shot.compress(CompressFormat.PNG, 100, stream);
			} finally {
				IOTools.ignorantClose(stream);
			}
			return target;
		}
		throw new IllegalStateException("Cannot take screenshot in any way.");
	}

	private Bitmap shootActivity(final Activity activity) {
		final AtomicReference<Bitmap> viewShot = new AtomicReference<>();
		instrumentation.runOnMainSync(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override public void run() {
				View view = activity.getWindow().getDecorView();
				LOG.trace("Taking screenshot of {}: {}", activity, HumanReadables.describe(view));
				if (!view.isDrawingCacheEnabled()) {
					view.setDrawingCacheEnabled(true);
					view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
				}
				viewShot.set(view.getDrawingCache());
			}
		});
		return viewShot.get();
	}
}
