package net.twisterrob.inventory.android.test.actors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;

import android.app.Activity;

import androidx.annotation.IdRes;
import androidx.test.espresso.Espresso;
import androidx.test.runner.lifecycle.Stage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static net.twisterrob.android.test.espresso.EspressoExtensions.isActionBarSubTitle;
import static net.twisterrob.android.test.espresso.EspressoExtensions.isActionBarTitle;
import static net.twisterrob.android.test.espresso.EspressoExtensions.loopMainThreadUntilIdle;
import static net.twisterrob.android.test.espresso.EspressoExtensions.onActionBarDescendant;
import static net.twisterrob.android.test.espresso.EspressoExtensions.onActionMenuItem;
import static net.twisterrob.android.test.espresso.EspressoExtensions.rotateActivity;
import static net.twisterrob.android.test.espresso.EspressoExtensions.withMenuItemId;
import static net.twisterrob.android.test.junit.InstrumentationExtensions.getActivitiesByType;
import static net.twisterrob.android.test.junit.InstrumentationExtensions.getActivityInStage;
import static net.twisterrob.android.test.matchers.AndroidMatchers.isFinishing;
import static net.twisterrob.android.test.matchers.AndroidMatchers.isInStage;

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
	/**
	 * Assert that the activity is visible and it is the top-most one.
	 */
	public void assertIsInFront() {
		onView(isRoot()).perform(loopMainThreadUntilIdle()); // otherwise the assertion may fail
		assertThat(getActivityInStage(Stage.RESUMED), instanceOf(activityClass));
	}
	/**
	 * Assert that the activity is visible, but not the top-most one.
	 */
	public void assertIsOverlaid(Activity activity) {
		// No loopMainThreadUntilIdle(), because we would get:
		// > No activities in stage RESUMED. Did you forget to launch the activity. (test.getActivity() or similar)?
		// Due to the top-most activity being potentially outside of the app.
		assertThat(activity, isInStage(Stage.PAUSED));
	}
	/**
	 * Assert that the activity is not visible, but still in memory.
	 */
	public void assertIsInBackground(Activity activity) {
		// No loopMainThreadUntilIdle(), because we would get:
		// > No activities in stage RESUMED. Did you forget to launch the activity. (test.getActivity() or similar)?
		// Due to the top-most activity being potentially outside of the app.
		assertThat(activity, isInStage(Stage.STOPPED));
	}
	public void close() {
		Espresso.pressBack();
	}
	public void closeToKill() {
		Espresso.pressBackUnconditionally();
	}
}
