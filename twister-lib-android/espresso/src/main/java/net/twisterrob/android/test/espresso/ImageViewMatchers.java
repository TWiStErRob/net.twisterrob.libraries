package net.twisterrob.android.test.espresso;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.ImageView;

import net.twisterrob.java.utils.ReflectionTools;

public class ImageViewMatchers {

	@SuppressWarnings("unchecked")
	public static Matcher<View> hasDrawable(Matcher<? super Drawable> drawableMatcher) {
		return (Matcher<View>)(Matcher<?>)new FeatureMatcher<ImageView, Drawable>(
				drawableMatcher, "drawable", "drawable") {
			@Override protected Drawable featureValueOf(ImageView actual) {
				return actual.getDrawable();
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static Matcher<View> hasBitmap(final Matcher<? super Bitmap> bitmapMatcher) {
		return (Matcher<View>)(Matcher<?>)new FeatureMatcher<ImageView, Bitmap>(
				bitmapMatcher, "bitmap in drawable", "bitmap") {
			@Override protected Bitmap featureValueOf(ImageView actual) {
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

	public static Matcher<Bitmap> withPixelAt(
			final int x, final int y, Matcher<? super Integer> colorMatcher) {
		return new FeatureMatcher<Bitmap, Integer>(
				colorMatcher, "pixel at " + x + ", " + y, "pixel color") {
			@Override protected Integer featureValueOf(Bitmap actual) {
				return actual.getPixel(x, y);
			}
		};
	}
}
