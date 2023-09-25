package net.twisterrob.android.content.glide;

import java.util.IdentityHashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextViewDrawableTarget<R extends Drawable>
		implements Target<R>, Transition.ViewAdapter {

	private final @NonNull TextView view;
	private final @NonNull Location location;
	private final int width;
	private final int height;
	private final Map<SizeReadyCallback, Callback> internalCallbacks = new IdentityHashMap<>();

	private @Nullable Request request;

	public enum Location {
		LEFT,
		TOP,
		RIGHT,
		BOTTOM
	}

	public TextViewDrawableTarget(@NonNull TextView view, @NonNull Location location, int width, int height) {
		this.view = view;
		this.location = location;
		this.width = width;
		this.height = height;
	}

	@Override public View getView() {
		return view;
	}

	@Override public void setRequest(@Nullable Request request) {
		this.request = request;
	}
	
	@Override public @Nullable Request getRequest() {
		return request;
	}

	@Override public Drawable getCurrentDrawable() {
		return view.getCompoundDrawables()[location.ordinal()];
	}

	@Override public void onResourceReady(@NonNull R resource, @Nullable Transition<? super R> transition) {
		if (transition == null || !transition.transition(resource, this)) {
			set(resource);
		}
	}

	@Override public void setDrawable(Drawable drawable) {
		set(drawable);
	}
	@Override public void onLoadCleared(@Nullable Drawable placeholder) {
		set(placeholder);
	}
	@Override public void onLoadStarted(@Nullable Drawable placeholder) {
		set(placeholder);
	}
	@Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
		set(errorDrawable);
	}

	private void set(@Nullable Drawable drawable) {
		if (drawable != null) {
			drawable.setBounds(0, 0, width, height);
		}
		view.setCompoundDrawables(
				selectForLocation(drawable, Location.LEFT), 
				selectForLocation(drawable, Location.TOP), 
				selectForLocation(drawable, Location.RIGHT), 
				selectForLocation(drawable, Location.BOTTOM)
		);
	}

	private @Nullable Drawable selectForLocation(@Nullable Drawable drawable, @NonNull Location location) {
		return location == this.location? drawable : null;
	}

	@Override public void getSize(final @NonNull SizeReadyCallback cb) {
		if (0 <= width && 0 <= height) {
			cb.onSizeReady(width, height);
		} else {
			SizeReadyCallback internalCb = (width, height) -> {
				switch (location) {
					case LEFT:
					case RIGHT:
						cb.onSizeReady(Target.SIZE_ORIGINAL, height);
						break;
					case TOP:
					case BOTTOM:
						cb.onSizeReady(width, Target.SIZE_ORIGINAL);
						break;
				}
			};
			SizeDeterminerHackTarget<TextView, R> target = new SizeDeterminerHackTarget<>(view);
			internalCallbacks.put(cb, new Callback(cb, internalCb, target));
			target.getSize(internalCb);
		}
	}

	@Override public void removeCallback(@NonNull SizeReadyCallback cb) {
		Callback callback = internalCallbacks.remove(cb);
		if (callback != null) {
			callback.target.removeCallback(callback.internal);
		}
	}

	@Override public void onStart() {
		// No op, because there's nothing to do when lifecycle changes.
	}
	@Override public void onStop() {
		// No op, because there's nothing to do when lifecycle changes.
	}
	@Override public void onDestroy() {
		// No op, because there's nothing to do when lifecycle changes.
	}

	private static class Callback {
		final @NonNull SizeReadyCallback original;
		final @NonNull SizeReadyCallback internal;
		final @NonNull Target<?> target;
		private Callback(
				@NonNull SizeReadyCallback original,
				@NonNull SizeReadyCallback internal,
				@NonNull Target<?> target
		) {
			this.original = original;
			this.internal = internal;
			this.target = target;
		}
	}
}
