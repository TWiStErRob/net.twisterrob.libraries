package net.twisterrob.android.activity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiObjectNotFoundException;

import net.twisterrob.android.view.SelectionView.SelectionStatus;

import static net.twisterrob.android.test.espresso.EspressoExtensions.loopMainThreadForAtLeast;
import static net.twisterrob.android.test.espresso.EspressoExtensions.onRoot;

/**
 * @see CaptureImage
 */
@RunWith(AndroidJUnit4.class)
public class CaptureImageTest {

	@Rule
	public final CaptureImageActivityTestRule activity = new CaptureImageActivityTestRule();

	private final CaptureImageActivityActor captureImage = new CaptureImageActivityActor();

	@Before public void setUp() {
		captureImage.assumeHasCamera();
	}

	@Ignore("https://github.com/TWiStErRob/net.twisterrob.libraries/issues/9")
	@Test public void rotationKeepsTheImage() throws UiObjectNotFoundException {
		captureImage.clearPreferences();
		activity.launchActivity(null);
		captureImage.allowPermissions();

		captureImage.take();
		onRoot().perform(loopMainThreadForAtLeast(1000));
		// Note: this becomes focused only if the camera can take a focused picture.
		// So this test will fail if the phone is on the table with camera facing down.
		captureImage.verifyState(SelectionStatus.FOCUSED);

		captureImage.rotate();

		// TODO not sure what to wait for, Glide is working, but clearly not started at this point.
		onRoot().perform(loopMainThreadForAtLeast(3000));
		captureImage.verifyNotErrorImage();
		// TODO this should be focused: https://github.com/TWiStErRob/net.twisterrob.libraries/issues/2
		captureImage.verifyState(SelectionStatus.NORMAL);
	}
}
