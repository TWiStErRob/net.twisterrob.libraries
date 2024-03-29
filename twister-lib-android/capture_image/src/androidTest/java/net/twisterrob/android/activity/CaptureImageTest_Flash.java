package net.twisterrob.android.activity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObjectNotFoundException;

/**
 * @see CaptureImage
 */
@RunWith(AndroidJUnit4.class)
public class CaptureImageTest_Flash {

	@Rule
	public final CaptureImageActivityTestRule activity = new CaptureImageActivityTestRule();

	private final CaptureImageActivityActor captureImage = new CaptureImageActivityActor();

	@Before public void setUp() {
		captureImage.assumeHasCamera();
		captureImage.assumeCameraHasFlash();
	}

	// Can't use @Before, because it messes with ActivityTestRule
	private void launchCaptureImageActivity() throws UiObjectNotFoundException {
		captureImage.clearPreferences();
		activity.launchActivity(null);
		captureImage.allowPermissions();
	}

	@Test public void flashStateRememberedBetweenLaunches_off() throws UiObjectNotFoundException {
		launchCaptureImageActivity();
		captureImage.turnFlashOn();
		captureImage.turnFlashOff();
		activity.finishActivity();

		activity.launchActivity(null);

		captureImage.assertFlashOff(activity);
	}

	@Test public void flashStateRememberedBetweenLaunches_on() throws UiObjectNotFoundException {
		launchCaptureImageActivity();
		captureImage.turnFlashOn();
		activity.finishActivity();

		activity.launchActivity(null);

		captureImage.assertFlashOn(activity);
	}

	@Test public void flashStateRememberedOnRotation_off() throws UiObjectNotFoundException {
		launchCaptureImageActivity();
		captureImage.turnFlashOn();
		captureImage.turnFlashOff();

		captureImage.rotate();

		captureImage.assertFlashOff(activity);
	}

	@Test public void flashStateRememberedOnRotation_on() throws UiObjectNotFoundException {
		launchCaptureImageActivity();
		captureImage.turnFlashOn();

		captureImage.rotate();

		captureImage.assertFlashOn(activity);
	}
}
