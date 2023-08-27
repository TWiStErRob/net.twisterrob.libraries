package net.twisterrob.android.test.espresso.actions;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralLocation;

/**
 * Translates a {@link CoordinatesProvider} by the given x and y distances.
 * The distances are given in term of the screen pixels.
 * 1.0 means to translate by an amount equivalent to 1 pixel.
 * <br>
 * This is in contrast to {@link GeneralLocation#translate(CoordinatesProvider, float, float)},
 * which translates by a fraction of the view's width and height.
 *
 * @see GeneralLocation#translate(CoordinatesProvider, float, float)
 * @see net.twisterrob.android.test.espresso.ViewActions#clickRelativeScreen(float, float, CoordinatesProvider)
 * @see net.twisterrob.android.test.espresso.ViewActions#clickRelativeView(float, float, CoordinatesProvider)
 */
public class ScreenTranslatedCoordinatesProvider implements CoordinatesProvider {

	private final @NonNull CoordinatesProvider coordinatesProvider;
	private final float dx;
	private final float dy;

	/**
	 * Creates an instance of {@link ScreenTranslatedCoordinatesProvider}
	 *
	 * @param coordinatesProvider the {@link CoordinatesProvider} to translate
	 * @param dx the distance in x direction in pixels
	 * @param dy the distance in y direction in pixels
	 */
	public ScreenTranslatedCoordinatesProvider(
			float dx,
			float dy,
			@NonNull CoordinatesProvider coordinatesProvider
	) {
		this.coordinatesProvider = coordinatesProvider;
		this.dx = dx;
		this.dy = dy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float[] calculateCoordinates(@NonNull View view) {
		float[] xy = coordinatesProvider.calculateCoordinates(view);
		xy[0] += dx;
		xy[1] += dy;
		return xy;
	}
}
