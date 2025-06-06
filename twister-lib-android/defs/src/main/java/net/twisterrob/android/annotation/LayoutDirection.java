package net.twisterrob.android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import androidx.annotation.IntDef;

import net.twisterrob.java.annotations.DebugHelper;

@IntDef(value = {
		android.util.LayoutDirection.RTL,
		android.util.LayoutDirection.LTR,
		android.util.LayoutDirection.LOCALE,
		android.util.LayoutDirection.INHERIT
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface LayoutDirection {
	class Converter {
		@DebugHelper
		public static String toString(@LayoutDirection int dir) {
			switch (dir) {
				case android.util.LayoutDirection.RTL:
					return "RTL";
				case android.util.LayoutDirection.LTR:
					return "LTR";
				case android.util.LayoutDirection.LOCALE:
					return "LOCALE";
				case android.util.LayoutDirection.INHERIT:
					return "INHERIT";
			}
			return "layoutDirection::" + dir;
		}
	}
}
