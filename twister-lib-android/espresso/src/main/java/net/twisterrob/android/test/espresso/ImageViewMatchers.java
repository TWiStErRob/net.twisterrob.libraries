package net.twisterrob.android.test.espresso;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.twisterrob.java.utils.ReflectionTools;

public class ImageViewMatchers {

	@SuppressWarnings("unchecked")
	public static @NonNull Matcher<View> withDrawable(@NonNull Matcher<? super Drawable> drawableMatcher) {
		return (Matcher<View>)(Matcher<?>)new FeatureMatcher<ImageView, Drawable>(
				drawableMatcher, "drawable", "drawable") {
			@Override protected @Nullable Drawable featureValueOf(@NonNull ImageView actual) {
				return actual.getDrawable();
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static @NonNull Matcher<Drawable> withColor(@NonNull Matcher<? super Integer> colorMatcher) {
		return (Matcher<Drawable>)(Matcher<?>)new FeatureMatcher<ColorDrawable, Integer>(
				colorMatcher, "drawable color", "color") {
			@Override protected @ColorInt @NonNull Integer featureValueOf(@NonNull ColorDrawable actual) {
				return actual.getColor();
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static @NonNull Matcher<View> withBitmap(final @NonNull Matcher<? super Bitmap> bitmapMatcher) {
		return (Matcher<View>)(Matcher<?>)new FeatureMatcher<ImageView, Bitmap>(
				bitmapMatcher, "bitmap in drawable", "bitmap") {
			@Override protected @Nullable Bitmap featureValueOf(@NonNull ImageView actual) {
				Drawable drawable = actual.getDrawable();
				while (drawable instanceof LayerDrawable) {
					// In case Glide is animating from thumbnail (0) to main result (1), use result.
					drawable = ((LayerDrawable)drawable).getDrawable(1);
				}
				if (drawable == null) {
					return null;
				}
				try {
					// This should cover the following:
					//  * public android.graphics.drawable.BitmapDrawable.getBitmap(): Bitmap
					//  * public com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable.getBitmap(): Bitmap
					//  * public any.other.third.party.SomeBitmapDrawable.getBitmap(): Bitmap
					// without having to actually depend on Glide.
					Method getBitmap = ReflectionTools.findDeclaredMethod(drawable.getClass(), "getBitmap");
					return (Bitmap)getBitmap.invoke(drawable);
				} catch (IllegalAccessException ex) {
					throw new AssertionError(ex);
				} catch (InvocationTargetException ex) {
					throw new AssertionError(ex);
				} catch (NoSuchMethodException ex) {
					throw new AssertionError(ex);
				}
			}
		};
	}

	public static @NonNull Matcher<Bitmap> withPixelAt(
			final int x, final int y, @NonNull Matcher<? super Integer> colorMatcher) {
		return new FeatureMatcher<Bitmap, Integer>(
				colorMatcher, "pixel at " + x + ", " + y, "pixel color") {
			@Override protected @ColorInt @NonNull Integer featureValueOf(@NonNull Bitmap actual) {
				return actual.getPixel(x, y);
			}
		};
	}
}
