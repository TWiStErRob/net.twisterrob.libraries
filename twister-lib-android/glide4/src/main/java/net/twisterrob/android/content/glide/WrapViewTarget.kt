package net.twisterrob.android.content.glide;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WrapViewTarget<Z> extends WrappingTarget<Z> {
	private final ImageView imageView;
	private final Target<Object> subsitute;
	/**
	 * {@link ImageView} must be part of the hierarchy, it's parent will be used to measure the Glide load.
	 * The size of it will match parent when it has no contents and wrap content when the resource is set in it.
	 * It is suggested that the image view has {@link ImageView#setAdjustViewBounds(boolean)} as {@code true}.
	 * @param target containing the view
	 */
	public WrapViewTarget(ImageViewTarget<? super Z> target) {
		super(target);
		imageView = target.getView();
		subsitute = new SizeDeterminerHackTarget<>((View)imageView.getParent());
	}
	@Override public void getSize(final SizeReadyCallback cb) {
		// Using the parent for sizing because otherwise the placeholder would be measured.
		// For example: if that's a ColorDrawable it has no intrinsic size and the ImageView lays out as 1x1 
		subsitute.getSize(cb);
	}
	@Override public void onLoadFailed(@Nullable Drawable errorDrawable) {
		update(LayoutParams.MATCH_PARENT);
		super.onLoadFailed(errorDrawable);
	}
	@Override public void onResourceReady(@NonNull Z resource, @Nullable Transition<? super Z> transition) {
		update(LayoutParams.WRAP_CONTENT);
		super.onResourceReady(resource, transition);
	}
	@Override public void onLoadCleared(Drawable placeholder) {
		update(LayoutParams.MATCH_PARENT);
		super.onLoadCleared(placeholder);
	}
	@Override public void onLoadStarted(Drawable placeholder) {
		update(LayoutParams.MATCH_PARENT);
		super.onLoadStarted(placeholder);
	}
	protected void update(int size) {
		imageView.layout(0, 0, 0, 0);
		LayoutParams params = imageView.getLayoutParams();
		params.width = size;
		params.height = size;
		imageView.setLayoutParams(params);
	}
}
