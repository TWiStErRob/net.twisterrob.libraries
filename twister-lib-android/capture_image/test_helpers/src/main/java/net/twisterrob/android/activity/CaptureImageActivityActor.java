package net.twisterrob.android.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.VerificationMode;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiObjectNotFoundException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import net.twisterrob.android.capture_image.R;
import net.twisterrob.android.view.CameraPreview;
import net.twisterrob.android.view.SelectionView;
import net.twisterrob.android.view.SelectionView.SelectionStatus;
import net.twisterrob.inventory.android.test.actors.ActivityActor;
import net.twisterrob.java.io.IOTools;

import static net.twisterrob.android.test.automators.AndroidAutomator.acceptAnyPermissions;
import static net.twisterrob.android.test.espresso.EspressoExtensions.loopMainThreadUntilIdle;
import static net.twisterrob.android.test.espresso.EspressoExtensions.onRoot;
import static net.twisterrob.android.test.espresso.ImageViewMatchers.withBitmap;
import static net.twisterrob.android.test.espresso.ImageViewMatchers.withDrawable;
import static net.twisterrob.android.test.espresso.ImageViewMatchers.withPixelAt;
import static net.twisterrob.android.test.matchers.AndroidMatchers.checkIfUnchecked;
import static net.twisterrob.android.test.matchers.AndroidMatchers.uncheckIfChecked;

/**
 * @see CaptureImage
 */
public class CaptureImageActivityActor extends ActivityActor {

	public CaptureImageActivityActor() {
		super(CaptureImage.class);
	}

	public Matcher<Intent> intendCamera(File file, Bitmap mockText) throws IOException {
		Uri resultFile = Uri.fromFile(file);
		Context context = InstrumentationRegistry.getInstrumentation().getContext();
		OutputStream output = context.getContentResolver().openOutputStream(resultFile);
		try {
			boolean compressed = mockText.compress(CompressFormat.PNG, 100, output);
			assumeTrue("Cannot save bitmap to " + resultFile, compressed);
		} finally {
			IOTools.ignorantClose(output);
		}
		Matcher<Intent> expectedIntent = hasComponent(CaptureImage.class.getName());
		intending(expectedIntent)
				.respondWith(new ActivityResult(Activity.RESULT_OK, new Intent(null, resultFile)));
		return expectedIntent;
	}

	@SuppressLint("ApplySharedPref") // want to save persist immediately
	public void clearPreferences() {
		ApplicationProvider
				.getApplicationContext()
				.getSharedPreferences(CaptureImage.class.getName(), Context.MODE_PRIVATE)
				.edit()
				.clear()
				.commit()
		;
	}

	public void allowPermissions() throws UiObjectNotFoundException {
		acceptAnyPermissions();
	}

	@SuppressWarnings("deprecation")
	public void assumeHasCamera() {
		assumeThat(
				"Device has a camera",
				android.hardware.Camera.getNumberOfCameras(),
				greaterThan(0)
		);
	}

	@SuppressWarnings("deprecation")
	public void assumeCameraHasFlash() {
		android.hardware.Camera camera = android.hardware.Camera.open(0);
		try {
			assumeThat(
					"Camera has a flash",
					camera.getParameters().getSupportedFlashModes(),
					hasItems(
							android.hardware.Camera.Parameters.FLASH_MODE_ON,
							android.hardware.Camera.Parameters.FLASH_MODE_OFF
					)
			);
		} finally {
			camera.release();
		}
	}

	@SuppressWarnings("deprecation")
	public void assertFlashOn(@NonNull androidx.test.rule.ActivityTestRule<CaptureImage> activity) {
		// TODO check drawable
		onView(withId(R.id.btn_flash)).check(matches(isChecked()));
		assertFlashMode(activity, android.hardware.Camera.Parameters.FLASH_MODE_ON);
	}

	@SuppressWarnings("deprecation")
	public void assertFlashOff(@NonNull androidx.test.rule.ActivityTestRule<CaptureImage> activity) {
		// TODO check drawable
		onView(withId(R.id.btn_flash)).check(matches(isNotChecked()));
		assertFlashMode(activity, android.hardware.Camera.Parameters.FLASH_MODE_OFF);
	}

	public void turnFlashOn() {
		onView(withId(R.id.btn_flash)).perform(checkIfUnchecked());
	}

	public void turnFlashOff() {
		onView(withId(R.id.btn_flash)).perform(uncheckIfChecked());
	}

	@SuppressWarnings("deprecation")
	private static void assertFlashMode(
			@SuppressWarnings("deprecation")
			@NonNull androidx.test.rule.ActivityTestRule<CaptureImage> activityRule,
			@Nullable String expectedMode
	) {
		// Wait in case there's an Activity recreation in progress, e.g. after a rotation.
		onRoot().perform(loopMainThreadUntilIdle());
		CaptureImage activity = activityRule.getActivity();
		CameraPreview preview = activity.findViewById(R.id.preview);
		assertThat(preview.getCamera(), notNullValue());
		String flashMode = preview.getCamera().getParameters().getFlashMode();
		assertEquals(expectedMode, flashMode);
	}

	public void verifyState(SelectionStatus status) {
		onView(isAssignableFrom(SelectionView.class))
				.check(matches(isCompletelyDisplayed()))
				.check(matches(selectionStatus(status)));
	}

	@SuppressWarnings("unchecked")
	private static @NonNull Matcher<View> selectionStatus(final SelectionStatus status) {
		return (Matcher<View>)(Matcher<?>)new FeatureMatcher<SelectionView, SelectionStatus>(
				equalTo(status), "selection status", "selection status") {
			@Override protected SelectionStatus featureValueOf(SelectionView actual) {
				return actual.getSelectionStatus();
			}
		};
	}

	public PickDialogActor pick() {
		onView(withId(R.id.btn_pick)).perform(click());
		return new PickDialogActor();
	}

	public void take() {
		onView(withId(R.id.btn_capture)).perform(click());
	}

	public void crop() {
		onView(withId(R.id.btn_crop)).perform(click());
	}

	public Uri createFakeImage(File fakeFile, @ColorInt int color) throws IOException {
		Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
		bitmap.setPixel(0, 0, color);
		FileOutputStream stream = new FileOutputStream(fakeFile);
		try {
			bitmap.compress(CompressFormat.PNG, 100, stream);
		} finally {
			stream.close();
		}
		return Uri.fromFile(fakeFile);
	}

	public void verifyNoImage() {
		onView(withId(R.id.image)).check(matches(withBitmap(nullValue())));
	}

	public void verifyImageColor(Matcher<? super Integer> colorMatcher) {
		onView(withId(R.id.image)).check(matches(withBitmap(withPixelAt(0, 0, colorMatcher))));
	}

	// TODO hasDrawable(R.drawable.image_error)
	private static final Matcher<Object> ERROR_DRAWABLE = allOf(
			instanceOf(LayerDrawable.class), // contains two layers of RotateDrawables
			not(instanceOf(TransitionDrawable.class)) // not glide animation
	);

	public void verifyErrorImage() {
		onView(withId(R.id.image)).check(matches(withDrawable(ERROR_DRAWABLE)));
	}

	public void verifyNotErrorImage() {
		onView(withId(R.id.image)).check(matches(not(withDrawable(ERROR_DRAWABLE))));
	}

	public void intendExternalChooser(Uri fakeUri) {
		Intents.intending(hasAction(Intent.ACTION_CHOOSER))
		       .respondWith(new ActivityResult(Activity.RESULT_OK, new Intent().setData(fakeUri)));
	}

	public void intendExternalChooserCancelled() {
		Intents.intending(hasAction(Intent.ACTION_CHOOSER))
		       .respondWith(new ActivityResult(Activity.RESULT_CANCELED, null));
	}

	public void verifyExternalChooser() {
		verifyExternalChooser(times(1));
	}
	public void verifyExternalChooser(int times) {
		verifyExternalChooser(times(times));
	}
	private void verifyExternalChooser(VerificationMode mode) {
		String title = ApplicationProvider.getApplicationContext().getString(R.string.image__choose_external__title);
		Intents.intended(allOf(
				hasAction(Intent.ACTION_CHOOSER),
				hasExtra(Intent.EXTRA_TITLE, title)
		), mode);
	}
}
