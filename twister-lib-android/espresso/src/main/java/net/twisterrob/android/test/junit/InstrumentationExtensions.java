package net.twisterrob.android.test.junit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Looper;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitor;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import kotlin.collections.CollectionsKt;

import static androidx.test.espresso.core.internal.deps.guava.base.Throwables.throwIfUnchecked;
import static androidx.test.platform.app.InstrumentationRegistry.getArguments;

public class InstrumentationExtensions {
	public static @NonNull Stage getActivityStage(final @NonNull Activity activity) {
		return callOnMainIfNecessary(new Callable<Stage>() {
			@Override public Stage call() {
				return ActivityLifecycleMonitorRegistry.getInstance().getLifecycleStageOf(activity);
			}
		});
	}
	public static @NonNull Collection<Activity> getActivitiesInStage(final @NonNull Stage stage) {
		return callOnMainIfNecessary(new Callable<Collection<Activity>>() {
			@Override public Collection<Activity> call() {
				return ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(stage);
			}
		});
	}
	public static @NonNull <T extends Activity> Collection<T> getActivitiesByType(@NonNull Class<T> activityType) {
		Collection<Activity> activities = getAllActivities();
		List<T> result = new ArrayList<>();
		for (Activity activity : activities) {
			if (activityType.isInstance(activity)) {
				result.add(activityType.cast(activity));
			}
		}
		return result;
	}
	/**
	 * @throws NoSuchElementException if there's no such activity
	 * @throws IllegalArgumentException if there are multiple activities
	 */
	public static @NonNull <T extends Activity> T getActivityByType(@NonNull Class<T> activityType)
			throws NoSuchElementException, IllegalArgumentException {
		Collection<T> activities = getActivitiesByType(activityType);
		return CollectionsKt.single(activities);
	}

	public static @NonNull Collection<Activity> getAllActivities() {
		return callOnMainIfNecessary(new Callable<Collection<Activity>>() {
			@Override public Collection<Activity> call() {
				ActivityLifecycleMonitor monitor = ActivityLifecycleMonitorRegistry.getInstance();
				List<Activity> activities = new ArrayList<>();
				for (Stage stage : Stage.values()) {
					activities.addAll(monitor.getActivitiesInStage(stage));
				}
				return activities;
			}
		});
	}
	public static @NonNull Map<Stage, Collection<Activity>> getAllActivitiesByStage() {
		return callOnMainIfNecessary(new Callable<Map<Stage, Collection<Activity>>>() {
			@Override public Map<Stage, Collection<Activity>> call() {
				ActivityLifecycleMonitor monitor = ActivityLifecycleMonitorRegistry.getInstance();
				Map<Stage, Collection<Activity>> activities = new EnumMap<>(Stage.class);
				for (Stage stage : Stage.values()) {
					activities.put(stage, monitor.getActivitiesInStage(stage));
				}
				return activities;
			}
		});
	}

	/**
	 * @throws NoSuchElementException if there's no such activity
	 * @throws IllegalArgumentException if there are multiple activities
	 */
	public static @NonNull Activity getActivityInStage(@NonNull Stage stage)
			throws NoSuchElementException, IllegalArgumentException {
		Collection<Activity> activities = getActivitiesInStage(stage);
		return CollectionsKt.single(activities);
	}

	/**
	 * @return {@code null} if there's no such activity
	 * @throws IllegalArgumentException if there are multiple activities
	 */
	public static @Nullable Activity tryGetActivityInStage(@NonNull Stage stage) throws IllegalArgumentException {
		Collection<Activity> activities = getActivitiesInStage(stage);
		return activities.isEmpty()? null : CollectionsKt.single(activities);
	}

	@AnyThread
	public static <T> T callOnMainIfNecessary(final @NonNull Callable<T> resultProvider) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			return callNow(resultProvider);
		} else {
			return callOnMain(resultProvider);
		}
	}
	@AnyThread
	public static <T> T callOnMain(final @NonNull Callable<T> resultProvider) {
		Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
		instrumentation.waitForIdleSync();
		final AtomicReference<T> ref = new AtomicReference<>();
		instrumentation.runOnMainSync(new Runnable() {
			@Override public void run() {
				ref.set(callNow(resultProvider));
			}
		});
		return ref.get();
	}
	@AnyThread
	private static <T> T callNow(@NonNull Callable<T> resultProvider) {
		try {
			return resultProvider.call();
		} catch (Exception e) {
			throwIfUnchecked(e);
			throw new RuntimeException("Unexpected exception", e);
		}
	}

	@AnyThread
	public static void runOnMainIfNecessary(final @NonNull Runnable action) {
		if (Looper.myLooper() == Looper.getMainLooper()) {
			action.run();
		} else {
			runOnMain(action);
		}
	}
	@AnyThread
	public static void runOnMain(final @NonNull Runnable action) {
		Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
		instrumentation.waitForIdleSync();
		instrumentation.runOnMainSync(action);
	}

	public static boolean getBooleanArgument(String key, boolean defaultValue) {
		String value = getArguments().getString(key);
		return value != null? Boolean.parseBoolean(value) : defaultValue;
	}

	public static @NonNull <T extends Activity> Collection<T> getActivitiesByTypeInStage(
			Class<? extends T> activityType, Stage stage) {
		Collection<? extends T> byType =
				InstrumentationExtensions.getActivitiesByType(activityType);
		Collection<T> inStage = new ArrayList<>();
		for (T activity : byType) {
			if (getActivityStage(activity) == stage) {
				inStage.add(activity);
			}
		}
		return inStage;
	}
}
