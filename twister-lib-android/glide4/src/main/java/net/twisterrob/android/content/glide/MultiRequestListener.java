package net.twisterrob.android.content.glide;

import java.util.Arrays;
import java.util.Collection;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MultiRequestListener<R> implements RequestListener<R> {
	private final Collection<? extends RequestListener<R>> listeners;

	@SuppressWarnings("varargs")
	@SafeVarargs
	public MultiRequestListener(RequestListener<R>... listeners) {
		this(Arrays.asList(listeners));
	}

	public MultiRequestListener(Collection<? extends RequestListener<R>> listeners) {
		this.listeners = listeners;
	}

	@Override public boolean onResourceReady(@NonNull R resource, @NonNull Object model,
			Target<R> target, @NonNull DataSource dataSource, boolean isFirstResource) {
		for (RequestListener<R> listener : listeners) {
			if (listener != null && listener.onResourceReady(resource, model, target, dataSource,
					isFirstResource)) {
				return true;
			}
		}
		return false;
	}

	@Override public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model,
			@NonNull Target<R> target, boolean isFirstResource) {
		for (RequestListener<R> listener : listeners) {
			if (listener != null && listener.onLoadFailed(e, model, target, isFirstResource)) {
				return true;
			}
		}
		return false;
	}
}
