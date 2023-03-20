package net.twisterrob.android.test.junit.rules;

import org.junit.rules.ExternalResource;

import android.annotation.SuppressLint;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.internal.runner.junit4.statement.UiThreadStatement;

import static androidx.test.espresso.core.internal.deps.guava.base.Throwables.throwIfUnchecked;

import net.twisterrob.android.test.espresso.idle.AllActivitiesDestroyedIdlingResource;

import static net.twisterrob.android.test.espresso.EspressoExtensions.getUIControllerHack;

public class WaitForEverythingToDestroyRule extends ExternalResource {

	@Override protected void before() throws Throwable {
		waitForEverythingToDestroy();
	}

	@Override protected void after() {
		waitForEverythingToDestroy();
	}

	private void waitForEverythingToDestroy() {
		AllActivitiesDestroyedIdlingResource.finishAll();
		AllActivitiesDestroyedIdlingResource activities = new AllActivitiesDestroyedIdlingResource();
		IdlingRegistry.getInstance().register(activities);
		try {
			waitForIdleSync();
		} finally {
			IdlingRegistry.getInstance().unregister(activities);
		}
	}

	@SuppressWarnings("CatchMayIgnoreException")
	@SuppressLint("RestrictedApi")
	private void waitForIdleSync() {
		try {
			UiThreadStatement.runOnUiThread(getUIControllerHack()::loopMainThreadUntilIdle);
		} catch (Throwable ex) {
			throwIfUnchecked(ex);
		}
	}
}
