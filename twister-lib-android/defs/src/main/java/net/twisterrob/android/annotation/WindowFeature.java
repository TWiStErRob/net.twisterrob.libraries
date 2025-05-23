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
import android.os.Build.VERSION_CODES;
import android.view.Window;

import androidx.annotation.IntDef;
import androidx.core.view.WindowCompat;

import net.twisterrob.java.annotations.DebugHelper;

@SuppressWarnings("deprecation")
@SuppressLint("UseRequiresApi")
// It is revised for L or below only. Newer features are not supported yet.
@TargetApi(VERSION_CODES.LOLLIPOP)
@IntDef(value = {
		Window.FEATURE_OPTIONS_PANEL,
		Window.FEATURE_NO_TITLE,
		Window.FEATURE_PROGRESS,
		Window.FEATURE_LEFT_ICON,
		Window.FEATURE_RIGHT_ICON,
		Window.FEATURE_INDETERMINATE_PROGRESS,
		Window.FEATURE_CONTEXT_MENU,
		Window.FEATURE_CUSTOM_TITLE,
		Window.FEATURE_ACTION_BAR,
		WindowCompat.FEATURE_ACTION_BAR,
		Window.FEATURE_ACTION_BAR_OVERLAY,
		WindowCompat.FEATURE_ACTION_BAR_OVERLAY,
		Window.FEATURE_ACTION_MODE_OVERLAY,
		WindowCompat.FEATURE_ACTION_MODE_OVERLAY,
		Window.FEATURE_SWIPE_TO_DISMISS,
		Window.FEATURE_CONTENT_TRANSITIONS,
		Window.FEATURE_ACTIVITY_TRANSITIONS
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface WindowFeature {
	class Converter {
		@SuppressLint("SwitchIntDef") // TODEL https://issuetracker.google.com/issues/37107158
		@DebugHelper
		public static String toString(@WindowFeature int featureId) {
			switch (featureId) {
				case Window.FEATURE_OPTIONS_PANEL:
					return "FEATURE_OPTIONS_PANEL";
				case Window.FEATURE_NO_TITLE:
					return "FEATURE_NO_TITLE";
				case Window.FEATURE_PROGRESS:
					return "FEATURE_PROGRESS";
				case Window.FEATURE_LEFT_ICON:
					return "FEATURE_LEFT_ICON";
				case Window.FEATURE_RIGHT_ICON:
					return "FEATURE_RIGHT_ICON";
				case Window.FEATURE_INDETERMINATE_PROGRESS:
					return "FEATURE_INDETERMINATE_PROGRESS";
				case Window.FEATURE_CONTEXT_MENU:
					return "FEATURE_CONTEXT_MENU";
				case Window.FEATURE_CUSTOM_TITLE:
					return "FEATURE_CUSTOM_TITLE";
				case Window.FEATURE_ACTION_BAR:
					return "FEATURE_ACTION_BAR";
				case Window.FEATURE_ACTION_BAR_OVERLAY:
					return "FEATURE_ACTION_BAR_OVERLAY";
				case Window.FEATURE_ACTION_MODE_OVERLAY:
					return "FEATURE_ACTION_MODE_OVERLAY";
				case Window.FEATURE_SWIPE_TO_DISMISS:
					return "FEATURE_SWIPE_TO_DISMISS";
				case Window.FEATURE_CONTENT_TRANSITIONS:
					return "FEATURE_CONTENT_TRANSITIONS";
				case Window.FEATURE_ACTIVITY_TRANSITIONS:
					return "FEATURE_ACTIVITY_TRANSITIONS";
			}
			return "featureId::" + featureId;
		}
	}
}
