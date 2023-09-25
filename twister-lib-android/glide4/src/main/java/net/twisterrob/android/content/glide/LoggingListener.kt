package net.twisterrob.android.content.glide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.twisterrob.java.annotations.DebugHelper;

@DebugHelper
public class LoggingListener<R> implements RequestListener<R> {
	public interface ModelFormatter<T> {
		String toString(T model);

		static ModelFormatter<Integer> forResources(@NonNull Context context) {
			return model -> {
				try {
					return context.getResources().getResourceName(model)
					              .replace(context.getPackageName(), "app");
				} catch (NotFoundException ex) {
					return Integer.toHexString(model) + "=" + model;
				}
			};
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(LoggingListener.class);
	private final String type;
	private final ModelFormatter<? super Object> formatter;

	public LoggingListener(String type) {
		this(type, String::valueOf);
	}

	@SuppressWarnings("unchecked")
	public <T> LoggingListener(String type, ModelFormatter<? super T> formatter) {
		this.type = type;
		this.formatter = (ModelFormatter<? super Object>)formatter;
	}

	@Override public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<R> target, boolean isFirstResource) {
		LOG.warn("Cannot load {}@{} into {} (first={})", type, formatter.toString(model), target, isFirstResource, e);
		return false;
	}

	@Override public boolean onResourceReady(@NonNull R resource, @NonNull Object model, Target<R> target, @NonNull DataSource dataSource, boolean isFirstResource) {
		LOG.trace("Loaded {}@{} into {} (first={}, source={}) transcoded={}",
				type, formatter.toString(model), target, isFirstResource, dataSource, toString(resource));
		return false;
	}

	private @NonNull String toString(@Nullable Object resource) {
		if (resource instanceof Bitmap) {
			return "Bitmap(" + ((Bitmap)resource).getWidth() + "x" + ((Bitmap)resource).getHeight() + ")@"
					+ Integer.toHexString(System.identityHashCode(resource));
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
				&& resource instanceof VectorDrawable) {
			return "VectorDrawable(" + ((VectorDrawable)resource).getIntrinsicWidth() + "x" + ((VectorDrawable)resource).getIntrinsicHeight() + ")@"
					+ Integer.toHexString(System.identityHashCode(resource));
		} else if (resource instanceof BitmapDrawable) {
			return toString(((BitmapDrawable)resource).getBitmap()) + " in " 
					+ "BitmapDrawable@" + Integer.toHexString(System.identityHashCode(resource));
		} else if (resource == null) {
			return "null";
		} else {
			return resource.toString();
		}
	}
}
