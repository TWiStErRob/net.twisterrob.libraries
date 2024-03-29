package net.twisterrob.android.test.espresso;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.WindowManager;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;

import androidx.annotation.IdRes;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.Root;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.runner.lifecycle.Stage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.core.internal.deps.guava.base.Throwables.throwIfUnchecked;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import net.twisterrob.android.test.espresso.idle.ToastIdlingResource;
import net.twisterrob.android.utils.tools.ResourceTools;

import static net.twisterrob.android.test.espresso.EspressoExtensions.getRoots;
import static net.twisterrob.android.test.espresso.EspressoExtensions.hasRoot;
import static net.twisterrob.android.test.junit.InstrumentationExtensions.getActivitiesInStage;
import static net.twisterrob.android.test.junit.InstrumentationExtensions.getAllActivitiesByStage;
import static net.twisterrob.android.test.matchers.AndroidMatchers.anyView;

public class DialogMatchers {
	public static final int BUTTON_POSITIVE = android.R.id.button1;
	public static final int BUTTON_NEGATIVE = android.R.id.button2;
	public static final int BUTTON_NEUTRAL = android.R.id.button3;

	public static Matcher<View> root(final Matcher<Root> matcher) {
		return new TypeSafeMatcher<View>() {
			@Override protected boolean matchesSafely(View view) {
				boolean isRoot = isRoot().matches(view);
				if (isRoot) {
					Root root = new Root.Builder()
							.withDecorView(view)
							.withWindowLayoutParams((WindowManager.LayoutParams)view.getLayoutParams())
							.build();
					return matcher.matches(root);
				}
				return false;
			}
			@Override public void describeTo(Description description) {
				description.appendText("is a root view and matches ").appendDescriptionOf(matcher);
			}
		};
	}

	/**
	 * Note: not using {@link androidx.test.espresso.matcher.RootMatchers#isSystemAlertWindow()},
	 * because that's broader than toast, but using the same mechanism to detect.
	 * @see <a href="http://stackoverflow.com/a/33387980/253468">
	 *     Checking toast message in android espresso</a>
	 * @see <a href="http://baroqueworksdev.blogspot.hu/2015/03/how-to-check-toast-window-on-android.html">
	 *     How to check Toast window, on android test-kit Espresso</a>
	 */
	@SuppressWarnings("deprecation") // TODO API 26 Toast handling may have changed, double-check APPLICATION_OVERLAY
	public static Matcher<Root> isToast() {
		return new WindowManagerLayoutParamTypeMatcher("is toast", WindowManager.LayoutParams.TYPE_TOAST);
	}
	public static void assertNoToastIsDisplayed() {
		// This is the original Espresso 2.2.2 code, but since 3.0.0 it takes 60 seconds to match
		//onView(isDialogMessage())
		//		.inRoot(isToast())
		//		.withFailureHandler(new PassMissingRoot())
		//		.check(matches(not(anything("toast root existed"))))
		//;
		for (Root root : getRoots()) {
			if (isToast().matches(root)) {
				// will fail and throw (used to get a similar message to previous version)
				doesNotExist().check(root.getDecorView(), null);
			}
		}
	}
	public static ViewAction waitForToastsToDisappear() {
		return new ViewAction() {
			@Override public Matcher<View> getConstraints() {
				return anyView();
			}
			@Override public String getDescription() {
				return "wait for Toast to disappear";
			}
			// this is called after an an onView, so the registration must sync for untilIdle to work
			// TODEL report this issue to Espresso, and change to IdlingRegistry.register
			@SuppressWarnings("deprecation")
			@Override public void perform(UiController uiController, View view) {
				ToastIdlingResource toastIdler = new ToastIdlingResource();
				try {
					Espresso.registerIdlingResources(toastIdler);
					uiController.loopMainThreadUntilIdle();
				} finally {
					Espresso.unregisterIdlingResources(toastIdler);
				}
			}
		};
	}

	public static Matcher<Root> isPopupMenu() {
		// Hardware key means for emulators that the following are present in hardware-qemu.ini:
		// hw.mainKeys = true, hw.trackBall = true, hw.dPad = true, hw.keyboard = false
		// On old Genymotion devices this was also true, but for emulators created with avdmanager,
		// it can still happen when not using a profile.
		return anyOf(
				// normal ActionBar compat overflow popup (e.g. on 5.0 without hardware key)
				isPlatformPopup(),
				// support bottom menu popup (e.g. on Genymotion 2.3.7 with hardware key)
				withDecorView(withClassName(is("android.support.v7.app.AppCompatDelegateImplV7$ListMenuDecorView"))),
				// androidx bottom menu popup 
				withDecorView(withClassName(is("androidx.appcompat.app.AppCompatDelegateImpl$ListMenuDecorView"))),
				// bottom menu popup (e.g. on Genymotion 4.1.1 with hardware key):
				// application-window-token=android.view.ViewRootImpl$W@537c56c8,
				// window-token=android.view.ViewRootImpl$W@53804678,
				// layout-params-string=WM.LayoutParams{(0,0)(wrapxwrap) gr=#51 ty=1003 fl=#1821000 fmt=-3 wanim=0x10301e4},
				// decor-view-string=DecorView{id=-1, ...}}
				new WindowManagerLayoutParamTypeMatcher("is bottom menu popup", TYPE_APPLICATION_ATTACHED_DIALOG, false)
		);
	}

	public static Matcher<View> isDialogView() {
		return root(isDialog());
	}

	public static Matcher<View> isDialogTitle() {
		// new androidx.appcompat.app.AlertDialog.Builder().setTitle(...).show();
		// @see androidx.appcompat.app.AlertController.setupTitle
		@IdRes int supportAlertTitle = ResourceTools.getIDResourceID(ApplicationProvider.getApplicationContext(), "alertTitle");
		// new android.app.AlertDialog.Builder().setTitle(...).show();
		@IdRes int androidAlertTitle = ResourceTools.getIDResourceID(null, "alertTitle");
		return anyOf(withId(supportAlertTitle), withId(androidAlertTitle));
	}

	public static Matcher<View> isDialogMessage() {
		return withId(android.R.id.message);
	}

	private static void clickInDialog(@IdRes int buttonId) {
		onView(withId(buttonId)).inRoot(isDialog()).perform(click());
	}
	public static void clickPositiveInDialog() {
		clickInDialog(BUTTON_POSITIVE);
	}
	public static void clickNegativeInDialog() {
		clickInDialog(BUTTON_NEGATIVE);
	}
	public static void clickNeutralInDialog() {
		clickInDialog(BUTTON_NEUTRAL);
	}
	public static void clickOutsideDialog() {
		// The ideal way would be:
		//onView(isRoot()).inRoot(isDialog()).perform(clickRelativeScreen(GeneralLocation.TOP_CENTER, 0, -16));
		// But that yields: https://stackoverflow.com/a/36986273/253468
		// InjectEventSecurityException: Check if Espresso is clicking outside the app
		// (system dialog, navigation bar if edge-to-edge is enabled, etc.).
		
		// As best effort, assuming the dialog is cancellable:
		Espresso.pressBack();
	}

	/**
	 * <pre><code>onView(isDialogView()).perform(clickPositive());</code></pre>
	 * @see #isDialogView()
	 */
	public static ViewAction clickPositive() {
		return new ClickInDialog(withId(BUTTON_POSITIVE));
	}
	/**
	 * <pre><code>onView(isDialogView()).perform(clickNegative());</code></pre>
	 * @see #isDialogView()
	 */
	public static ViewAction clickNegative() {
		return new ClickInDialog(withId(BUTTON_NEGATIVE));
	}
	/**
	 * <pre><code>onView(isDialogView()).perform(clickNeutral());</code></pre>
	 * @see #isDialogView()
	 */
	public static ViewAction clickNeutral() {
		return new ClickInDialog(withId(BUTTON_NEUTRAL));
	}

	public static void assertDialogIsDisplayed() {
		// Prevent RootViewPicker.waitForAtLeastOneActivityToBeResumed from waiting 60 seconds and then throwing:
		// No activities found. Did you forget to launch the activity [...]?
		// NoActivityResumedException: No activities in stage RESUMED. Did you forget to launch the activity. ...
		getInstrumentation().waitForIdleSync();
		if (getActivitiesInStage(Stage.RESUMED).isEmpty()) {
			throw new AssertionError(
					"No activities in stage RESUMED. Activities are " + getAllActivitiesByStage());
		}
		// Prevent RootViewPicker.pickARoot() from waiting 60 seconds and then throwing:
		// NoMatchingRootException: Matcher 'is dialog' did not match any of the following roots ...
		if (!hasRoot(isDialog())) {
			throw NoMatchingRootException.create(isDialog(), getRoots());
		}
		// both of the below statements should be equivalent
		onView(isRoot()).inRoot(isDialog()).check(matches(isCompletelyDisplayed()));
		onView(isRoot()).check(matches(root(isDialog())));
	}

	public static void assertNoDialogIsDisplayed() {
		// It's not possible to have a dialog shown without an activity, so prevent:
		// No activities found. Did you forget to launch the activity by calling getActivity() or startActivitySync or similar?
		getInstrumentation().waitForIdleSync(); // quick wait, don't care about idling resources
		if (getActivitiesInStage(Stage.RESUMED).isEmpty()) {
			return;
		}
		// required to use the root(...) option because these fail too early on inRoot(isDialog())
		//onView(isRoot()).inRoot(isDialog()).check(doesNotExist());
		//onView(isRoot()).inRoot(isDialog()).check(matches(not(isDisplayed())));
		// this works but the other is more concise
		//onView(isRoot()).check(matches(root(not(RootMatchers.isDialog()))));
		onView(isDialogView())
				/*.noActivity()*/
				// TODEL Workaround for https://github.com/android/android-test/issues/1739
				.withFailureHandler((error, __) -> {
					// Same as androidx.test.espresso.base.ThrowableHandler, but not package private.
					throwIfUnchecked(error);
					throw new RuntimeException(error);
				})
				.check(doesNotExist());
	}

	/**
	 * Some tests may leave open dialogs, because they match the message or fail during the dialog being open.
	 * Ensure in that case there's no exception logged:
	 * <pre>
	 * E/WindowManager: android.view.WindowLeaked: Activity ... has leaked window
	 * com.android.internal.policy.impl.PhoneWindow$DecorView{3816351a V.E..... R......D 0,0-1041,875}
	 * that was originally added here
	 *     at android.view.ViewRootImpl.<init>(ViewRootImpl.java:457)
	 *     ...
	 *     at androidx.appcompat.app.AlertDialog$Builder.show(AlertDialog.java:953)
	 * </pre>
	 */
	public static void attemptCloseDialog() {
		tryCloseDialog(false);
	}
	public static void ensureDialogClosed() {
		tryCloseDialog(true);
		assertNoDialogIsDisplayed(); // double-check ourselves
	}
	private static void tryCloseDialog(boolean wait) {
		if (!wait) {
			// It's not that important to close the dialog, so prevent
			// RootViewPicker.waitForAtLeastOneActivityToBeResumed from waiting ~40 seconds and then throwing:
			// No activities found. Did you forget to launch the activity [...]?
			getInstrumentation().waitForIdleSync(); // quick wait, don't care about idling resources
			if (getActivitiesInStage(Stage.RESUMED).isEmpty()) {
				return;
			}
		}
		if (hasRoot(isDialog())) {
			// press back button if there's a dialog displayed
			onView(isRoot()).inRoot(isDialog()).withFailureHandler(new Ignore()).perform(pressBack());
		}
		if (hasRoot(isDialog())) {
			// press negative button if there's still a dialog displayed
			onView(withId(BUTTON_NEGATIVE)).inRoot(isDialog()).withFailureHandler(new Ignore()).perform(click());
			// pressing the negative button will fail only if there's no dialog, or the button is not visible
			// both of these are suppressed via a failure handler
			// as this is a best effort implementation which should change how tests behave
			// (i.e. don't fail when there's no dialog; and if there's an invalid dialog, the next step will fail on it)
		}
	}

	private static class ClickInDialog implements ViewAction {
		private final Matcher<View> viewMatcher;
		public ClickInDialog(Matcher<View> viewMatcher) {
			this.viewMatcher = viewMatcher;
		}
		@Override public Matcher<View> getConstraints() {
			return allOf(isCompletelyDisplayed(), root(isDialog()));
		}
		@Override public String getDescription() {
			return "Click " + StringDescription.asString(viewMatcher) + " in a dialog.";
		}
		@Override public void perform(UiController uiController, View view) {
			Matcher<View> dialogSpecificMatcher = allOf(viewMatcher, isDescendantOfA(is(view)));
			for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
				if (dialogSpecificMatcher.matches(child)) {
					ViewAction click = click();
					if (!click.getConstraints().matches(child)) {
						throw new PerformException.Builder()
								.withActionDescription(click.getDescription())
								.withViewDescription(HumanReadables.describe(child))
								.build();
					}
					click.perform(uiController, child);
					return;
				}
			}
			throw new PerformException.Builder()
					.withActionDescription(this.getDescription())
					.withViewDescription(HumanReadables.describe(view))
					.withCause(new NoMatchingViewException.Builder()
							.withRootView(view)
							.withViewMatcher(dialogSpecificMatcher)
							.build()
					)
					.build();
		}
	}
}
