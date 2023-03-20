package net.twisterrob.android.activity;

import java.io.File;
import java.lang.reflect.Field;

import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import android.content.Context;
import android.content.Intent;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;

import net.twisterrob.android.test.espresso.idle.GlideIdlingResource;
import net.twisterrob.android.test.junit.SensibleActivityTestRule;

/**
 * <ul>
 *     <li>Provides control over when the activity is launched.
 *         Must call {@code activity.launchActivity(null)}.</li>
 *     <li>Provides an automatic temp folder.</li>
 *     <li>Provides an output file in temp folder.</li>
 *     <li>Provides a Glide idling resources for preview synchronization</li>
 * </ul>
 */
public class CaptureImageActivityTestRule extends SensibleActivityTestRule<CaptureImage> {

	private File outputFile;
	private final GlideIdlingResource glideIdler = new GlideIdlingResource();
	private final TemporaryFolder temp = new TemporaryFolder() {
		@Override protected void before() throws Throwable {
			super.before();
			outputFile = new File(getRoot(), "output.file");
		}
	};

	public CaptureImageActivityTestRule() {
		super(CaptureImage.class, true, false);
	}

	public File getOutputFile() {
		return outputFile;
	}

	public TemporaryFolder getTemp() {
		return temp;
	}

	@Override protected void afterActivityLaunched() {
		super.afterActivityLaunched();
		// Reset Intents framework state, so it ignores a call to ActivityTestRule#launchActivity().
		Intents.release();
		Intents.init();
	}

	@Override protected void beforeActivityLaunched() {
		resetGlide(InstrumentationRegistry.getInstrumentation().getTargetContext());
		IdlingRegistry.getInstance().register(glideIdler);
		super.beforeActivityLaunched();
	}

	@Override protected void afterActivityFinished() {
		super.afterActivityFinished();
		IdlingRegistry.getInstance().unregister(glideIdler);
		resetGlide(InstrumentationRegistry.getInstrumentation().getTargetContext());
	}

	@Override protected Intent getActivityIntent() {
		return new Intent()
				.putExtra(CaptureImage.EXTRA_OUTPUT, outputFile.getAbsolutePath());
	}

	@Override public Statement apply(Statement base, Description description) {
		base = super.apply(base, description);
		// Wrap activity rule in temp so that folder is available throughout.
		base = temp.apply(base, description);
		return base;
	}

	/** Try to get rid of references and clean as much as possible */
	@SuppressWarnings("deprecation")
	public static void resetGlide(final @NonNull Context applicationContext) {
		if (!Glide.isSetup()) {
			return;
		}
		InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
			@Override public void run() {
				try {
					Glide.with(applicationContext).onDestroy();
					Glide.get(applicationContext).clearMemory();
					Field glide = Glide.class.getDeclaredField("glide");
					glide.setAccessible(true);
					glide.set(null, null);
				} catch (Exception ex) {
					throw new IllegalStateException("Cannot tear down Glide", ex);
				}
			}
		});
	}
}
