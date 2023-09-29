package net.twisterrob.android.activity;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;

import android.graphics.Color;
import android.net.Uri;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObjectNotFoundException;

import net.twisterrob.android.view.SelectionView.SelectionStatus;

/**
 * Note: all tests have a different color to make sure there are no images loaded from cache.
 *
 * @see CaptureImage
 */
@RunWith(AndroidJUnit4.class)
public class CaptureImageTest_External {

	@Rule
	public final CaptureImageActivityTestRule activity = new CaptureImageActivityTestRule();

	private final CaptureImageActivityActor captureImage = new CaptureImageActivityActor();

	@Test public void loadsImageFromExternalSource()
			throws UiObjectNotFoundException, IOException {
		activity.launchActivity(null);
		captureImage.allowPermissions();
		Uri fakeUri = captureImage.createFakeImage(activity.getTemp().newFile(), Color.RED);
		captureImage.verifyState(SelectionStatus.NORMAL);
		PickDialogActor.PopupIntent<Uri> pick = captureImage.pick().pick();
		pick.intend(fakeUri);
		pick.open();
		pick.verify(1);
		captureImage.verifyState(SelectionStatus.FOCUSED);
		Intents.assertNoUnverifiedIntents();
		captureImage.verifyImageColor(equalTo(Color.RED));
	}

	@Test public void loadsImageFromExternalCamera()
			throws UiObjectNotFoundException, IOException {
		activity.launchActivity(null);
		captureImage.allowPermissions();
		Uri fakeUri = captureImage.createFakeImage(activity.getTemp().newFile(), Color.CYAN);
		captureImage.verifyState(SelectionStatus.NORMAL);
		PickDialogActor.PopupIntent<Uri> pick = captureImage.pick().captureFromCamera();
		pick.intend(fakeUri);
		pick.open();
		pick.verify(1);
		captureImage.verifyState(SelectionStatus.FOCUSED);
		Intents.assertNoUnverifiedIntents();
		captureImage.verifyImageColor(equalTo(Color.CYAN));
	}

	@Test public void fallsBackPreviousImageIfPickCancelled()
			throws UiObjectNotFoundException, IOException {
		activity.launchActivity(null);
		captureImage.allowPermissions();
		Uri fakeUri = captureImage.createFakeImage(activity.getTemp().newFile(), Color.GREEN);
		captureImage.verifyState(SelectionStatus.NORMAL);
		PickDialogActor.PopupIntent<Uri> pick1 = captureImage.pick().pick();
		pick1.intend(fakeUri);
		pick1.open();
		pick1.verify(1);
		captureImage.verifyState(SelectionStatus.FOCUSED);
		PickDialogActor.PopupIntent<Uri> pick2 = captureImage.pick().pick();
		pick2.intendCancelled();
		pick2.open();
		pick2.verify(2);
		captureImage.verifyState(SelectionStatus.BLURRY);
		Intents.assertNoUnverifiedIntents();
		captureImage.verifyImageColor(equalTo(Color.GREEN));
	}

	@Test public void doesNotFallBackToImageFromClosedActivityIfPickCancelled()
			throws UiObjectNotFoundException, IOException {
		activity.launchActivity(null);
		captureImage.allowPermissions();
		Uri fakeUri = captureImage.createFakeImage(activity.getTemp().newFile(), Color.BLUE);
		PickDialogActor.PopupIntent<Uri> pick1 = captureImage.pick().pick();
		pick1.intend(fakeUri);
		pick1.open();
		pick1.verify(1);
		Intents.assertNoUnverifiedIntents();
		activity.finishActivity();

		activity.launchActivity(null);
		captureImage.verifyState(SelectionStatus.NORMAL);
		PickDialogActor.PopupIntent<Uri> pick2 = captureImage.pick().pick();
		pick2.intendCancelled();
		pick2.open();
		pick2.verify(1);
		Intents.assertNoUnverifiedIntents();
		captureImage.verifyNoImage();
		captureImage.verifyState(SelectionStatus.BLURRY);
	}

	@Test public void doesNotFallBackToImageFromClosedActivityIfPickInvalid()
			throws UiObjectNotFoundException, IOException {
		activity.launchActivity(null);
		captureImage.allowPermissions();
		Uri fakeUri = captureImage.createFakeImage(activity.getTemp().newFile(), Color.YELLOW);
		PickDialogActor.PopupIntent<Uri> pick1 = captureImage.pick().pick();
		pick1.intend(fakeUri);
		pick1.open();
		pick1.verify(1);
		Intents.assertNoUnverifiedIntents();
		activity.finishActivity();

		activity.launchActivity(null);
		captureImage.verifyState(SelectionStatus.NORMAL);
		PickDialogActor.PopupIntent<Uri> pick2 = captureImage.pick().pick();
		pick2.intend(Uri.EMPTY);
		pick2.open();
		pick2.verify(1);
		Intents.assertNoUnverifiedIntents();
		captureImage.verifyErrorImage();
		captureImage.verifyState(SelectionStatus.BLURRY);
	}
}
