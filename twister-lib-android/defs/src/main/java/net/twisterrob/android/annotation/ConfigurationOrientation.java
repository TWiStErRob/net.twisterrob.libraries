package net.twisterrob.android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import android.content.res.Configuration;

import androidx.annotation.IntDef;

import net.twisterrob.java.annotations.DebugHelper;

@SuppressWarnings("deprecation")
@IntDef(value = {
		Configuration.ORIENTATION_UNDEFINED,
		Configuration.ORIENTATION_LANDSCAPE,
		Configuration.ORIENTATION_PORTRAIT,
		Configuration.ORIENTATION_SQUARE
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface ConfigurationOrientation {
	class Converter {
		@DebugHelper
		public static String toString(@ConfigurationOrientation int state) {
			switch (state) {
				case Configuration.ORIENTATION_UNDEFINED:
					return "ORIENTATION_UNDEFINED";
				case Configuration.ORIENTATION_LANDSCAPE:
					return "ORIENTATION_LANDSCAPE(land)";
				case Configuration.ORIENTATION_PORTRAIT:
					return "ORIENTATION_PORTRAIT(port)";
				case Configuration.ORIENTATION_SQUARE:
					return "ORIENTATION_SQUARE";
			}
			return "orientation::" + state;
		}
	}
}
