package net.twisterrob.android.utils.wrappers;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION_CODES;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import net.twisterrob.java.annotations.DebugHelper;

/**
 * Base class to allow easy override of methods for {@link EdgeEffect}.
 */
@RequiresApi(VERSION_CODES.ICE_CREAM_SANDWICH)
@DebugHelper
public class EdgeEffectWrapper extends EdgeEffect {

	private final @NonNull EdgeEffect effectImpl;

	/**
	 * @param effectImpl {@link EdgeEffectCompat.EdgeEffectImpl} object
	 *                   (type erased to {@link Object} because it's not public)
	 */
	public EdgeEffectWrapper(@NonNull Context context, @NonNull EdgeEffect effectImpl) {
		super(context);
		this.effectImpl = effectImpl;
	}

	public @NonNull EdgeEffect getWrapped() {
		return effectImpl;
	}

	@Override public void setSize(int width, int height) {
		effectImpl.setSize(width, height);
	}

	@Override public boolean isFinished() {
		return effectImpl.isFinished();
	}

	@Override public void finish() {
		effectImpl.finish();
	}

	@Override public void onPull(float deltaDistance) {
		effectImpl.onPull(deltaDistance);
	}

	@Override public void onRelease() {
		effectImpl.onRelease();
	}

	@Override public void onAbsorb(int velocity) {
		effectImpl.onAbsorb(velocity);
	}

	@Override public boolean draw(Canvas canvas) {
		return effectImpl.draw(canvas);
	}

	@RequiresApi(VERSION_CODES.LOLLIPOP)
	@Override public int getMaxHeight() {
		return effectImpl.getMaxHeight();
	}

	@RequiresApi(VERSION_CODES.LOLLIPOP)
	@Override public void setColor(int color) {
		effectImpl.setColor(color);
	}

	@RequiresApi(VERSION_CODES.LOLLIPOP)
	@Override public int getColor() {
		return effectImpl.getColor();
	}

	@RequiresApi(VERSION_CODES.LOLLIPOP)
	@Override public void onPull(float deltaDistance, float displacement) {
		effectImpl.onPull(deltaDistance, displacement);
	}
}
