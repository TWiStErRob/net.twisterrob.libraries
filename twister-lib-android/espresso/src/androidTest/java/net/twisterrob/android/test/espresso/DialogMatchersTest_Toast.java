package net.twisterrob.android.test.espresso;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThrows;
import static org.junit.internal.matchers.ThrowableMessageMatcher.hasMessage;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import junit.framework.AssertionFailedError;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import net.twisterrob.android.test.junit.InstrumentationExtensions;
import net.twisterrob.android.test.junit.TestPackageIntentRule;
import net.twisterrob.inventory.android.test.activity.TestActivity;

import static net.twisterrob.android.test.espresso.DialogMatchers.assertNoToastIsDisplayed;
import static net.twisterrob.android.test.espresso.DialogMatchers.isDialogMessage;
import static net.twisterrob.android.test.espresso.DialogMatchers.isToast;
import static net.twisterrob.android.test.espresso.DialogMatchers.waitForToastsToDisappear;
import static net.twisterrob.android.test.espresso.DialogMatchersTest.DIALOG_TIMEOUT;
import static net.twisterrob.android.test.espresso.DialogMatchersTest.ESPRESSO_BACKOFF_TIMEOUT;
import static net.twisterrob.android.test.espresso.EspressoExtensions.loopMainThreadUntilIdle;
import static net.twisterrob.android.test.espresso.EspressoExtensions.onRoot;
import static net.twisterrob.test.junit.Assert.assertTimeout;

@RunWith(AndroidJUnit4.class)
public class DialogMatchersTest_Toast {
	
	private static final Logger LOG = LoggerFactory.getLogger(DialogMatchersTest_Toast.class);

	@SuppressWarnings("deprecation")
	@Rule public final androidx.test.rule.ActivityTestRule<TestActivity> activity =
			new TestPackageIntentRule<>(TestActivity.class);

	private Toast shownToast;

	@Before public void preconditions() {
		onView(isRoot()).perform(waitForToastsToDisappear());
	}

	@After public void cancelToast() {
		// to reduce possibility of tests interacting
		if (shownToast != null) {
			shownToast.cancel();
		}
	}

	@Test(timeout = DIALOG_TIMEOUT) public void testAssertNoToastIsDisplayed_passes_whenNoToastShown() {
		assertNoToastIsDisplayed();
	}

	@Test(timeout = DIALOG_TIMEOUT) public void testIsToast_finds_shownToast() {
		Toast toast = createToast("Hello Toast!");
		assertNoToastIsDisplayed();
		show(toast);

		try {
			onRoot(isToast()).check(matches(isDisplayed()));
		} finally {
			toast.cancel();
		}

		assertNoToastIsDisplayed();
	}

	@LargeTest
	@Test(timeout = ESPRESSO_BACKOFF_TIMEOUT)
	public void testIsToast_onRootFails_whenNoToastShown() {
		assertNoToastIsDisplayed();

		NoMatchingRootException expectedFailure = assertThrows(NoMatchingRootException.class, new ThrowingRunnable() {
			@Override public void run() {
				onRoot(isToast()).check(matches(isDisplayed()));
			}
		});

		assertThat(expectedFailure,
				hasMessage(startsWith("Matcher 'is toast' did not match any of the following roots")));
	}

	@Test(timeout = DIALOG_TIMEOUT) public void testIsToast_worksAsRootMatcher() {
		Toast toast = createToast("Hello Toast!");
		assertNoToastIsDisplayed();
		show(toast);

		try {
			onView(isDialogMessage())
					.inRoot(isToast())
					.check(matches(withText(containsStringIgnoringCase("hello"))));
		} finally {
			toast.cancel();
		}

		assertNoToastIsDisplayed();
	}

	@LargeTest
	@Test(timeout = ESPRESSO_BACKOFF_TIMEOUT)
	public void testIsToast_inRootFails_whenNoToastShown() {
		assertNoToastIsDisplayed();

		NoMatchingRootException expectedFailure = assertThrows(NoMatchingRootException.class, new ThrowingRunnable() {
			@Override public void run() {
				onView(isDialogMessage())
						.inRoot(isToast())
						.check(matches(anything()));
			}
		});

		assertThat(expectedFailure,
				hasMessage(startsWith("Matcher 'is toast' did not match any of the following roots")));
	}

	@Test(timeout = DIALOG_TIMEOUT) public void testAssertNoToastIsDisplayed_fails_whenToastShown() {
		Toast toast = createToast("Dummy message");
		show(toast);

		AssertionFailedError expectedFailure = assertThrows(AssertionFailedError.class, new ThrowingRunnable() {
			// androidx.test.espresso.base.DefaultFailureHandler$AssertionFailedWithCauseError:
			// 'not toast root existed' doesn't match the selected view.
			// Expected: not toast root existed
			// Got: "LinearLayout{...}"
			// or
			// androidx.test.espresso.base.DefaultFailureHandler$AssertionFailedWithCauseError:
			// 'not toast root existed' doesn't match the selected view.
			// Expected: not toast root existed
			// Got: "TextView{text=Hello Toast!}"
			// at androidx.test.espresso.ViewInteraction.check(ViewInteraction.java:158)
			// at net.twisterrob.android.test.espresso.DialogMatchers.assertNoToastIsDisplayed(DialogMatchers.java:68)
			@Override public void run() {
				assertNoToastIsDisplayed();
			}
		});

		assertThat(expectedFailure, hasMessage(startsWith("View is present in the hierarchy")));
	}

	@Test public void testWaitForToastsToDisappear_waitsForToast() {
		Toast toast = createToast("A toast");
		toast.setDuration(Toast.LENGTH_SHORT);
		long show = System.currentTimeMillis();
		show(toast);
		onRoot(isToast()).check(matches(isDisplayed()));
		long verified = System.currentTimeMillis();

		// Based on com.android.server.notification.NotificationManagerService#SHORT_DELAY = 2000
		// Allow a bit more than double the set duration to show and hide the toast.
		// For some reason the delay is 2000, but on emulator it disappears in ~1900 ms.
		// Estimated time for the toast to disappear is a bit illogical, but hopefully more stable.
		long minimumTime = 2000 - (verified - show);
		LOG.trace("testWaitForToastsToDisappear_waitsForToast: minimumTime = {}", minimumTime);
		assertTimeout(minimumTime, 2 * 2000, TimeUnit.MILLISECONDS, new Runnable() {
			@Override public void run() {
				onView(isRoot()).perform(waitForToastsToDisappear());
			}
		});

		assertNoToastIsDisplayed();
	}

	private static Toast createToast(final @NonNull CharSequence message) {
		return InstrumentationExtensions.callOnMain(new Callable<Toast>() {
			@Override public Toast call() {
				return Toast.makeText(InstrumentationRegistry.getInstrumentation().getContext(), message, Toast.LENGTH_LONG);
			}
		});
	}

	private void show(@NonNull Toast toast) {
		toast.show();
		shownToast = toast;
		// wait for it to show, this is best effort to reduce flakyness
		onRoot().perform(loopMainThreadUntilIdle());
	}
}
