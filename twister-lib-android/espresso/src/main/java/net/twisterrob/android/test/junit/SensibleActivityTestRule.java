package net.twisterrob.android.test.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import android.app.Activity;
import android.content.*;
import android.util.Log;

import androidx.annotation.*;
import androidx.test.espresso.intent.rule.IntentsRule;

import net.twisterrob.android.test.espresso.ScreenshotFailure;
import net.twisterrob.android.test.junit.rules.ChattyLogCatRule;
import net.twisterrob.android.test.junit.rules.DeviceUnlockerRule;
import net.twisterrob.android.test.junit.rules.SystemAnimationsRule;
import net.twisterrob.android.test.junit.rules.WaitForEverythingToDestroyRule;

@SuppressWarnings("deprecation")
public class SensibleActivityTestRule<T extends Activity> extends androidx.test.rule.ActivityTestRule<T> {
	private static final String TAG = "ViewInteraction";

	private Intent startIntent;

	public SensibleActivityTestRule(Class<T> activityClass) {
		super(activityClass);
	}
	public SensibleActivityTestRule(Class<T> activityClass, boolean initialTouchMode) {
		super(activityClass, initialTouchMode);
	}
	public SensibleActivityTestRule(Class<T> activityClass, boolean initialTouchMode, boolean launchActivity) {
		super(activityClass, initialTouchMode, launchActivity);
	}

	// LIFECYCLE: with the base = foo.apply(base) pattern, these will be executed in reverse when evaluate() is called.
	@Override public Statement apply(Statement base, Description description) {
		// LIFECYCLE: @Before @Test @After will be executed logically at this point in the chain.

		// Inner of ActivityTestRule, because it needs to take a screenshot before a failure finishes the activity.
		base = new ScreenshotFailure().apply(base, description);
		// Inner of ActivityTestRule, because it should be run after the Activity has started.
		base = new IntentsRule().apply(base, description);
		// This needs to be right before the super call, so it is the immediate inner of ActivityTestRule.
		base = new TestLogger().apply(base, description);

		// ActivityTestRule itself.
		base = super.apply(base, description);
		// LIFECYCLE: Anything after this point will be called BEFORE the activity is launched!

		// Outside of ActivityTestRule, because it should be run before the Activity has started.
		base = new DeviceUnlockerRule(true).apply(base, description);
		base = new WaitForEverythingToDestroyRule().apply(base, description);
		base = new SystemAnimationsRule().apply(base, description);
		base = new ChattyLogCatRule().apply(base, description);

		// Anything from above will be wrapped inside the name shortener so that all exceptions are cleaned.
		//base = new PackageNameShortener().apply(base, description); // TODO make it available

		// LIFECYCLE: Logically, this point will execute first when entering a test from the test runner.
		return base;
	}

	public @NonNull Intent getStartIntent() {
		return startIntent;
	}

	/**
	 * Makes sure we have the intent passed in to launchActivity as a non-null and keep the reference to it.
	 * This is the only way to capture what the actual launched intent was,
	 * so in {@link #beforeActivityLaunched()} we can set up the intent extras via {@link #getStartIntent()}.
	 */
	@Override public T launchActivity(@Nullable Intent startIntent) {
		Intent intent = startIntent;
		if (intent == null) {
			intent = getActivityIntent();
		}
		if (intent == null) {
			intent = new Intent();
		}
		this.startIntent = intent;
		return super.launchActivity(this.startIntent);
	}

	@CallSuper
	@Override protected void beforeActivityLaunched() {
		Log.i(TAG, "Launching activity at the beginning of test.");
		super.beforeActivityLaunched();
	}

	@CallSuper
	@Override protected void afterActivityLaunched() {
		Log.d(TAG, "Activity launched at the beginning of test.");
		super.afterActivityLaunched();
	}

	@CallSuper
	@Override protected void afterActivityFinished() {
		Log.d(TAG, "Finished Activity at the end of the test.");
		super.afterActivityFinished();
	}

	/**
	 * This needs to be applied right inside {@link androidx.test.rule.ActivityTestRule.ActivityStatement}
	 * so the logging happens at the correct time.
	 */
	@SuppressWarnings("JavadocReference")
	private static class TestLogger implements TestRule {
		@Override public Statement apply(final Statement base, Description description) {
			return new Statement() {
				@Override public void evaluate() throws Throwable {
					try {
						base.evaluate();
					} finally {
						Log.i(TAG, "Finishing activity at the end of test.");
					}
				}
			};
		}
	}
}
