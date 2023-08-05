package net.twisterrob.inventory.android.test.actors;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.*;

import android.view.View;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static net.twisterrob.android.test.espresso.DialogMatchers.*;

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
