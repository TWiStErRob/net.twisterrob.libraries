package net.twisterrob.android.content.glide;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Listener which updates the {@link ImageView} to be software rendered,
 * because {@link com.caverock.androidsvg.SVG SVG}/{@link android.graphics.Picture Picture}
 * can't render on a hardware backed {@link android.graphics.Canvas Canvas}.
 *
 * @param <R> not used, exists to prevent unchecked warnings at usage
 */
public class SoftwareLayerSetter<R> implements RequestListener<R> {
	private final @NonNull TargetViewGetter getter;

	interface TargetViewGetter {
		@NonNull View getView(Target<?> target);

		@SuppressWarnings("deprecation")
		// TODEL https://github.com/bumptech/glide/issues/3332 For now RequestBuilder.into returns this.
		TargetViewGetter VIEW_TARGET = target ->
				((com.bumptech.glide.request.target.ViewTarget<?, ?>)target).getView();

		TargetViewGetter CUSTOM_VIEW_TARGET = target ->
				((CustomViewTarget<?, ?>)target).getView();
	}

	public SoftwareLayerSetter() {
		this(TargetViewGetter.VIEW_TARGET);
	}
	public SoftwareLayerSetter(@NonNull TargetViewGetter getter) {
		this.getter = getter;
	}

	@Override public boolean onResourceReady(@NonNull R resource, @NonNull Object model,
			Target<R> target, @NonNull DataSource dataSource, boolean isFirstResource) {
		View view = getter.getView(target);
		view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		return false;
	}

	@Override public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model,
			@NonNull Target<R> target, boolean isFirstResource) {
		View view = getter.getView(target);
		view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		return false;
	}
}
