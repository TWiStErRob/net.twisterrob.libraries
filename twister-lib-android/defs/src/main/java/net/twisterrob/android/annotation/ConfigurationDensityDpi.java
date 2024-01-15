package net.twisterrob.android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import android.annotation.SuppressLint;
import android.content.res.Configuration;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;

import net.twisterrob.java.annotations.DebugHelper;

@SuppressLint("InlinedApi")
@IntDef(value = {
		Configuration.DENSITY_DPI_UNDEFINED,
		// @hide Configuration.DENSITY_DPI_ANY
		0xfffe,
		// @hide Configuration.DENSITY_DPI_NONE
		0xffff
})
@IntRange(from = 0)
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface ConfigurationDensityDpi {
	class Converter {
		/**
		 * @see Configuration#DENSITY_DPI_ANY which is @hide
		 */
		@SuppressWarnings("JavadocReference")
		private static final int DENSITY_DPI_ANY = 0xfffe;

		/**
		 * @see Configuration#DENSITY_DPI_NONE which is @hide
		 */
		@SuppressWarnings("JavadocReference")
		private static final int DENSITY_DPI_NONE = 0xffff;

		@SuppressLint({"SwitchIntDef", "WrongConstant"})
		@DebugHelper
		public static String toString(@ConfigurationDensityDpi int dpi) {
			switch (dpi) {
				case Configuration.DENSITY_DPI_UNDEFINED:
					return "DENSITY_DPI_UNDEFINED";
				case DENSITY_DPI_ANY:
					return "DENSITY_DPI_ANY";
				case DENSITY_DPI_NONE:
					return "DENSITY_DPI_NONE(nodpi)";
			}
			return "dpi::" + dpi;
		}
	}
}
