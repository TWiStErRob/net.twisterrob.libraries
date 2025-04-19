package net.twisterrob.android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.DisplayMetrics;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;

import net.twisterrob.java.annotations.DebugHelper;

@SuppressLint("UniqueConstants")
@IntDef(value = {
		Configuration.DENSITY_DPI_UNDEFINED,
		DisplayMetrics.DENSITY_LOW,
		DisplayMetrics.DENSITY_MEDIUM,
		DisplayMetrics.DENSITY_TV,
		DisplayMetrics.DENSITY_HIGH,
		DisplayMetrics.DENSITY_XHIGH,
		DisplayMetrics.DENSITY_400,
		DisplayMetrics.DENSITY_XXHIGH,
		DisplayMetrics.DENSITY_560,
		DisplayMetrics.DENSITY_XXXHIGH,
		DisplayMetrics.DENSITY_DEFAULT
})
@IntRange(from = 1)
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface Density {
	@SuppressLint("WrongConstant") // REPORT false positive on FORMAT in static { }.
	class Converter {
		private static final DecimalFormat FORMAT = new DecimalFormat("#.###");

		static {
			FORMAT.setRoundingMode(RoundingMode.HALF_UP);
		}

		@SuppressLint("UseRequiresApi")
		// It is revised for N or below only. Newer features are not supported yet.
		@TargetApi(VERSION_CODES.N)
		@DebugHelper
		public static String toString(@Density int density) {
			StringBuilder name = new StringBuilder(getName(density));
			if (name.length() == 0) {
				name.append("density::").append(density);
			}
			float multiplier = density / (float)DisplayMetrics.DENSITY_DEFAULT;
			synchronized (FORMAT) {
				name.append("@").append(FORMAT.format(multiplier)).append("x");
			}
			if (DisplayMetrics.DENSITY_DEFAULT == density) {
				//name.append("(default)");
			}
			if (VERSION_CODES.N <= VERSION.SDK_INT && DisplayMetrics.DENSITY_DEVICE_STABLE == density) {
				name.append("(device)");
			}
			return name.toString();
		}

		private static String getName(@Density int state) {
			switch (state) {
				case Configuration.DENSITY_DPI_UNDEFINED:
					return "DENSITY_DPI_UNDEFINED";
				case DisplayMetrics.DENSITY_LOW:
					return "DENSITY_LOW";
				case DisplayMetrics.DENSITY_MEDIUM:
					return "DENSITY_MEDIUM";
				case DisplayMetrics.DENSITY_TV:
					return "DENSITY_TV";
				case DisplayMetrics.DENSITY_HIGH:
					return "DENSITY_HIGH";
				case DisplayMetrics.DENSITY_XHIGH:
					return "DENSITY_XHIGH";
				case DisplayMetrics.DENSITY_400:
					return "DENSITY_400";
				case DisplayMetrics.DENSITY_XXHIGH:
					return "DENSITY_XXHIGH";
				case DisplayMetrics.DENSITY_560:
					return "DENSITY_560";
				case DisplayMetrics.DENSITY_XXXHIGH:
					return "DENSITY_XXXHIGH";
				//case DisplayMetrics.DENSITY_DEFAULT:
				default:
					return "";
			}
		}
	}
}
