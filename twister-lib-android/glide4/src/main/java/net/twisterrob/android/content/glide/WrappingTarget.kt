package net.twisterrob.android.content.glide;

import android.graphics.drawable.Drawable;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WrappingTarget<R> implements Target<R> {
	@NonNull protected final Target<? super R> wrapped;
	public WrappingTarget(@NonNull Target<? super R> wrapped) {
		this.wrapped = wrapped;
	}

	@Override public void getSize(SizeReadyCallback cb) {
		wrapped.getSize(cb);
	}
	@Override public void onLoadStarted(Drawable placeholder) {
		wrapped.onLoadStarted(placeholder);
	}
	@Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
		wrapped.onLoadFailed(errorDrawable);
	}
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override public void onResourceReady(@NonNull R resource, @Nullable Transition<? super R> transition) {
		wrapped.onResourceReady(resource, (Transition)transition);
	}
	@Override public void onLoadCleared(Drawable placeholder) {
		wrapped.onLoadCleared(placeholder);
	}

	@Override public void removeCallback(@NonNull SizeReadyCallback cb) {
		wrapped.removeCallback(cb);
	}

	@Override public Request getRequest() {
		return wrapped.getRequest();
	}
	@Override public void setRequest(Request request) {
		wrapped.setRequest(request);
	}

	@Override public void onStart() {
		wrapped.onStart();
	}
	@Override public void onStop() {
		wrapped.onStop();
	}
	@Override public void onDestroy() {
		wrapped.onDestroy();
	}
	@Override public int hashCode() {
		return wrapped.hashCode();
	}
	@Override public boolean equals(Object o) {
		if (o instanceof WrappingTarget) {
			return this.wrapped.equals(((WrappingTarget<?>)o).wrapped);
		}
		return super.equals(o);
	}
	@Override public @NonNull String toString() {
		return "Wrapped " + wrapped;
	}
}
