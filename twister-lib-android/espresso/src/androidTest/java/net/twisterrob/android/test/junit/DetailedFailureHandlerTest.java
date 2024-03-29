package net.twisterrob.android.test.junit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThrows;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.espresso.base.DefaultFailureHandler;

import net.twisterrob.android.test.junit.AndroidJUnitRunner.DetailedFailureHandler;
import net.twisterrob.inventory.android.test.activity.TestActivity;

import static net.twisterrob.test.hamcrest.Matchers.containsStackTrace;
import static net.twisterrob.test.hamcrest.Matchers.hasMessage;
import static net.twisterrob.test.hamcrest.Matchers.stackClass;
import static net.twisterrob.test.hamcrest.Matchers.stackMethod;

public class DetailedFailureHandlerTest {

	@SuppressWarnings("deprecation")
	@Rule public final androidx.test.rule.ActivityTestRule<TestActivity> activity =
			new TestPackageIntentRule<>(TestActivity.class);

	@Test public void testNoActivityResumedExceptionHasRealCause() {
		DefaultFailureHandler defaultHandler = new DefaultFailureHandler(ApplicationProvider.getApplicationContext());
		try {
			Espresso.setFailureHandler(new DetailedFailureHandler(defaultHandler));

			Throwable expectedFailure = assertThrows(NoActivityResumedException.class, new ThrowingRunnable() {
				@Override public void run() {
					Espresso.pressBack();
				}
			});

			assertThat(expectedFailure, hasMessage("Pressed back and killed the app"));
			assertThat(expectedFailure, containsStackTrace(
					// Espresso's public cause for this exception
					allOf(stackClass("androidx.test.espresso.ViewInteraction"), stackMethod("perform")),
					allOf(stackClass("androidx.test.espresso.ViewInteraction"),
							stackMethod("waitForAndHandleInteractionResults")),
					// Real cause in Espresso's internals, helps debugging if shown
					allOf(stackClass("androidx.test.espresso.action.PressBackAction"), stackMethod("perform")),
					allOf(stackClass("androidx.test.espresso.action.KeyEventActionBase"),
							stackMethod("waitForPendingForegroundActivities"))
			));
		} finally {
			Espresso.setFailureHandler(defaultHandler);
		}
	}

	@Test public void testDefaultFailureHandlerDoesNotHaveInternalDetails() {
		Throwable expectedFailure = assertThrows(NoActivityResumedException.class, new ThrowingRunnable() {
			@Override public void run() {
				Espresso.pressBack();
			}
		});

		assertThat(expectedFailure, hasMessage("Pressed back and killed the app"));
		assertThat(expectedFailure, containsStackTrace(
				// Espresso's public cause for this exception
				allOf(stackClass("androidx.test.espresso.ViewInteraction"), stackMethod("perform")),
				allOf(stackClass("androidx.test.espresso.ViewInteraction"),
						stackMethod("waitForAndHandleInteractionResults")),
				// Real cause in Espresso's internals, helps debugging if shown
				not(allOf(stackClass("androidx.test.espresso.action.PressBackAction"), stackMethod("perform"))),
				not(allOf(stackClass("androidx.test.espresso.action.KeyEventActionBase"),
						stackMethod("waitForPendingForegroundActivities")))
		));
	}
}
