package net.twisterrob.android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentCallbacks2;
import android.os.Build.VERSION_CODES;

import androidx.annotation.IntDef;

import net.twisterrob.java.annotations.DebugHelper;

@SuppressLint("UseRequiresApi")
// It is revised for J or below only. Newer features are not supported yet.
@TargetApi(VERSION_CODES.JELLY_BEAN)
@IntDef(value = {
		ComponentCallbacks2.TRIM_MEMORY_COMPLETE,
		ComponentCallbacks2.TRIM_MEMORY_MODERATE,
		ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
		ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN,
		ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
		ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
		ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
@SuppressWarnings("deprecation") // Most of these levels are not notified since Android 34.
public @interface TrimMemoryLevel {
	class Converter {
		/** @see ComponentCallbacks2 */
		@DebugHelper
		public static String toString(@TrimMemoryLevel int level) {
			switch (level) {
				case ComponentCallbacks2.TRIM_MEMORY_COMPLETE: // Deprecated
					return "TRIM_MEMORY_COMPLETE";
				case ComponentCallbacks2.TRIM_MEMORY_MODERATE: // Deprecated
					return "TRIM_MEMORY_MODERATE";
				case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
					return "TRIM_MEMORY_BACKGROUND";
				case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
					return "TRIM_MEMORY_UI_HIDDEN";
				case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL: // Deprecated
					return "TRIM_MEMORY_RUNNING_CRITICAL";
				case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW: // Deprecated
					return "TRIM_MEMORY_RUNNING_LOW";
				case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE: // Deprecated
					return "TRIM_MEMORY_RUNNING_MODERATE";
			}
			return "trimMemoryLevel::" + level;
		}
	}
}
