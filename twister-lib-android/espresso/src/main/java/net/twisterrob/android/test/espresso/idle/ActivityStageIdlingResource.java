package net.twisterrob.android.test.espresso.idle;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.StringDescription.asString;

import android.app.Activity;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleCallback;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import net.twisterrob.android.test.espresso.EspressoExtensions;

import static net.twisterrob.android.test.espresso.EspressoExtensions.onRoot;

public class ActivityStageIdlingResource extends AsyncIdlingResource {
	private final ActivityLifecycleCallback callback = new ActivityLifecycleCallback() {
		@Override public void onActivityLifecycleChanged(Activity activity, Stage stage) {
			if (activity == ActivityStageIdlingResource.this.activity
					&& stageMatcher.matches(stage)) {
				monitor.removeLifecycleCallback(callback);
				transitionToIdle();
			}
		}
	};

	private final ActivityLifecycleMonitor monitor;
	private final Activity activity;
	private final Matcher<Stage> stageMatcher;

	public ActivityStageIdlingResource(
			Activity activity,
			Matcher<Stage> stageMatcher) {
		this.activity = activity;
		this.stageMatcher = stageMatcher;
		this.monitor = ActivityLifecycleMonitorRegistry.getInstance();
	}

	@Override public String getName() {
		return "ActivityStageIdlingResource[" + activity + " " + asString(stageMatcher) + "]";
	}

	private boolean isIdleCore() {
		Stage currentStage = monitor.getLifecycleStageOf(activity);
		return stageMatcher.matches(currentStage);
	}

	@Override protected boolean isIdle() {
		monitor.removeLifecycleCallback(callback);
		return isIdleCore();
	}

	@Override protected void waitForIdleAsync() {
		monitor.addLifecycleCallback(callback);
	}

	public static void waitForAtLeast(Activity activity, Stage stage) {
		ActivityStageIdlingResource resource =
				new ActivityStageIdlingResource(activity, greaterThanOrEqualTo(stage));
		IdlingRegistry.getInstance().register(resource);
		try {
			// TODO AndroidX, onIdle doesn't sync registry, so it doesn't wait for the just-registered resource
			//Espresso.onIdle();
			onRoot().perform(EspressoExtensions.loopMainThreadUntilIdle());
		} finally {
			IdlingRegistry.getInstance().unregister(resource);
		}
	}
}
