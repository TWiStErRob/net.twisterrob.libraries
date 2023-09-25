package net.twisterrob.android.content.glide;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * {@link com.bumptech.glide.request.target.CustomViewTarget.SizeDeterminer} is not exposed,
 * so we have to use the public interface to get its behavior.
 * <p>
 * No implementations (error) of any methods, because they shouldn't be ever called.
 * Use only {@link #getSize(SizeReadyCallback)} and {@link #removeCallback(SizeReadyCallback)}.
 */
@SuppressWarnings("JavadocReference")
class SizeDeterminerHackTarget<T extends View, Z> extends CustomViewTarget<T, Z> {
	public SizeDeterminerHackTarget(@NonNull T view) {
		super(view);
	}

	@Override protected void onResourceLoading(@Nullable Drawable placeholder) {
		throw new UnsupportedOperationException();
	}

	@Override public void onResourceReady(@NonNull Z resource,
			@Nullable Transition<? super Z> transition) {
		throw new UnsupportedOperationException();
	}

	@Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
		throw new UnsupportedOperationException();
	}

	@Override protected void onResourceCleared(@Nullable Drawable placeholder) {
		throw new UnsupportedOperationException();
	}
}
