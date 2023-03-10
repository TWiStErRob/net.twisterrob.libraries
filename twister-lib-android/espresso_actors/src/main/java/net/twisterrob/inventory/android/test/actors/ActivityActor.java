package net.twisterrob.inventory.android.test.actors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import android.app.Activity;

import androidx.annotation.IdRes;
import androidx.test.espresso.Espresso;
import androidx.test.runner.lifecycle.Stage;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static net.twisterrob.android.test.espresso.EspressoExtensions.*;
import static net.twisterrob.android.test.junit.InstrumentationExtensions.*;
import static net.twisterrob.android.test.matchers.AndroidMatchers.*;

public class ActivityActor {
	private final Class<? extends Activity> activityClass;
	public ActivityActor(Class<? extends Activity> activityClass) {
		this.activityClass = activityClass;
	}

	protected void clickActionOverflow(@IdRes int menuItemId) {
		onActionMenuItem(withMenuItemId(menuItemId)).perform(click());
	}
	protected void clickActionBar(@IdRes int viewId) {
		onActionBarDescendant(withId(viewId)).perform(click());
	}

	protected void assertActionTitle(String name) {
		onView(isActionBarTitle()).check(matches(withText(containsString(name))));
	}
	protected void assertActionSubTitle(String name) {
		onView(isActionBarSubTitle()).check(matches(withText(containsString(name))));
	}

	public void assertClosing() {
		assertClosing(activityClass);
	}
	public void assertClosing(Activity activity) {
		// Synchronize with Espresso in case there's something going on.
		Espresso.onIdle();
		assertThat(activity, instanceOf(activityClass));
		assertThat(activity, isFinishing());
	}
	protected <T extends Activity> void assertClosing(Class<T> activityType) {
		// Synchronize with Espresso in case there's something going on.
		Espresso.onIdle();
		// there may be other activities still not fully destroyed, so let's loop
		for (T activity : getActivitiesByType(activityType)) {
			assertThat(activity, isFinishing());
		}
	}
	public void rotate() {
		onView(isRoot()).perform(rotateActivity());
	}
	public void assertIsInFront() {
		onView(isRoot()).perform(loopMainThreadUntilIdle()); // otherwise the assertion may fail
		assertThat(getActivityInStage(Stage.RESUMED), instanceOf(activityClass));
	}
	public void assertIsInBackground(Activity activity) {
		assertThat(activity, isInStage(Stage.PAUSED));
	}
	public void close() {
		Espresso.pressBack();
	}
	public void closeToKill() {
		Espresso.pressBackUnconditionally();
	}
}
