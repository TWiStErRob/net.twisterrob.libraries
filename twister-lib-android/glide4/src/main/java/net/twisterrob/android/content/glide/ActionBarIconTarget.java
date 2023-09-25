package net.twisterrob.android.content.glide;

import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import net.twisterrob.android.utils.tools.ResourceTools;

public class ActionBarIconTarget implements Target<Drawable> {
	private final ActionBar actionBar;
	private @Nullable Request request;

	public ActionBarIconTarget(ActionBar actionBar) {
		this.actionBar = actionBar;
	}

	@Override public void onLoadStarted(Drawable placeholder) {
		actionBar.setIcon(placeholder);
	}
	@Override public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
		// Transitions are not supported because it's not "possible" to get the icon/logo view from an Action Bar/ToolBar.
		actionBar.setIcon(resource);
	}
	@Override public void onLoadCleared(Drawable placeholder) {
		actionBar.setIcon(placeholder);
	}
	@Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
		actionBar.setIcon(errorDrawable);
	}

	@Override public void getSize(SizeReadyCallback cb) {
		int size;
		TypedValue tv = new TypedValue();
		if (actionBar.getThemedContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			DisplayMetrics metrics = actionBar.getThemedContext().getResources().getDisplayMetrics();
			size = TypedValue.complexToDimensionPixelSize(tv.data, metrics);
		} else {
			size = ResourceTools.dipInt(actionBar.getThemedContext(), 48); // standard size
		}
		cb.onSizeReady(size, size);
	}

	@Override public void removeCallback(@NonNull SizeReadyCallback cb) {
		// No op, because we don't hold a reference to cb in getSize.
	}

	@Override public void setRequest(@Nullable Request request) {
		this.request = request;
	}
	@Override public @Nullable Request getRequest() {
		return request;
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
}
