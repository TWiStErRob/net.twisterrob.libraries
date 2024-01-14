package net.twisterrob.inventory.android.test.actors;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.matchesPattern;

import android.view.View;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static net.twisterrob.android.test.espresso.DialogMatchers.assertDialogIsDisplayed;
import static net.twisterrob.android.test.espresso.DialogMatchers.assertNoDialogIsDisplayed;
import static net.twisterrob.android.test.espresso.DialogMatchers.assertNoToastIsDisplayed;
import static net.twisterrob.android.test.espresso.DialogMatchers.clickNeutralInDialog;
import static net.twisterrob.android.test.espresso.DialogMatchers.isDialogMessage;
import static net.twisterrob.android.test.espresso.DialogMatchers.isDialogTitle;
import static net.twisterrob.android.test.espresso.DialogMatchers.isToast;

public class AlertDialogActor {
	protected static void assertDialogTitle(Matcher<View> matcher) {
		onView(isDialogTitle())
				.check(matches(matcher));
	}

	protected final void assertDialogMessage(Matcher<View> matcher) {
		onView(withText(matchesPattern("%\\d"))).check(doesNotExist());
		onView(isDialogMessage())
				.check(matches(isCompletelyDisplayed()))
				.check(matches(matcher))
		;
	}

	protected final void assertToastMessage(Matcher<View> matcher) {
		onView(isDialogMessage())
				.inRoot(isToast())
				.check(matches(isCompletelyDisplayed()))
				.check(matches(matcher))
		;
	}

	public void assertNoToastDisplayed() {
		assertNoToastIsDisplayed();
	}

	public void assertNotDisplayed() {
		assertNoDialogIsDisplayed();
	}

	public void assertDisplayed() {
		assertDialogIsDisplayed();
	}

	protected void dismissWithNeutral() {
		clickNeutralInDialog();
		assertNoDialogIsDisplayed();
	}
}
