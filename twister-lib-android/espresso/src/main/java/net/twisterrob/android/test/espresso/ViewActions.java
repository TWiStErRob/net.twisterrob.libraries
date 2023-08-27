package net.twisterrob.android.test.espresso;

import android.view.InputDevice;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;

import static androidx.test.espresso.action.ViewActions.actionWithAssertions;

import net.twisterrob.android.test.espresso.actions.ScreenTranslatedCoordinatesProvider;

public final class ViewActions {

	private ViewActions() {
		throw new AssertionError("No instances.");
	}

	/**
	 * Translates a {@link CoordinatesProvider} by the given {@param offsetX} and {@param offsetY} distances.
	 * The distances are given in term of the view's size.
	 * 1.0 means to translate by an amount equivalent to the view's length.
	 *
	 * @param side    which side of the view to click on, use {@link GeneralLocation} constants.
	 * @param offsetX by how much to offset the click, use negative values for left, positive for right.
	 * @param offsetY by how much to offset the click, use negative values for top, positive for bottom.
	 * @see #clickRelativeScreen(CoordinatesProvider, float, float) for a version that uses absolute pixels.
	 */
	public static @NonNull ViewAction clickRelativeView(@NonNull CoordinatesProvider side, float offsetX, float offsetY) {
		return actionWithAssertions(
				new GeneralClickAction(
						Tap.SINGLE,
						GeneralLocation.translate(side, offsetX, offsetY),
						Press.FINGER,
						InputDevice.SOURCE_UNKNOWN,
						MotionEvent.BUTTON_PRIMARY
				)
		);
	}

	/**
	 * Translates a {@link CoordinatesProvider} by the given {@param offsetX} and {@param offsetY} distances.
	 * The distances are given in term of the screen's pixels.
	 * 1.0 means to translate by an amount equivalent to 1 pixel, not {@code dp}.
	 *
	 * @param side    which side of the view to click on, use {@link GeneralLocation} constants.
	 * @param offsetX by how much to offset the click, use negative values for left, positive for right.
	 * @param offsetY by how much to offset the click, use negative values for top, positive for bottom.
	 * @see #clickRelativeView(CoordinatesProvider, float, float) for a version that is based on view size.
	 */
	public static @NonNull ViewAction clickRelativeScreen(@NonNull CoordinatesProvider side, float offsetX, float offsetY) {
		return actionWithAssertions(
				new GeneralClickAction(
						Tap.SINGLE,
						new ScreenTranslatedCoordinatesProvider(side, offsetX, offsetY),
						Press.FINGER,
						InputDevice.SOURCE_UNKNOWN,
						MotionEvent.BUTTON_PRIMARY
				)
		);
	}
}
