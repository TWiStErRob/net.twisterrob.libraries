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

import static android.content.res.Configuration.UI_MODE_NIGHT_MASK;
import static android.content.res.Configuration.UI_MODE_TYPE_MASK;

import androidx.annotation.IntDef;

import net.twisterrob.java.annotations.DebugHelper;

@SuppressLint("UniqueConstants")
@IntDef(value = {
		Configuration.UI_MODE_TYPE_MASK,
		Configuration.UI_MODE_TYPE_UNDEFINED,
		Configuration.UI_MODE_TYPE_NORMAL,
		Configuration.UI_MODE_TYPE_DESK,
		Configuration.UI_MODE_TYPE_CAR,
		Configuration.UI_MODE_TYPE_TELEVISION,
		Configuration.UI_MODE_TYPE_APPLIANCE,
		Configuration.UI_MODE_TYPE_WATCH,

		Configuration.UI_MODE_NIGHT_MASK,
		Configuration.UI_MODE_NIGHT_UNDEFINED,
		Configuration.UI_MODE_NIGHT_NO,
		Configuration.UI_MODE_NIGHT_YES
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface ConfigurationUIMode {
	class Converter {
		@SuppressWarnings("WrongConstant")
		@DebugHelper
		public static String toString(@ConfigurationUIMode int uiMode) {
			String type = getType(uiMode & Configuration.UI_MODE_TYPE_MASK);
			String night = getNight(uiMode & Configuration.UI_MODE_NIGHT_MASK);
			String result = type + "|" + night;
			int remainder = uiMode & ~(UI_MODE_TYPE_MASK | UI_MODE_NIGHT_MASK);
			if (remainder != 0) {
				result += "|" + "unknown" + "(" + Integer.toHexString(remainder) + ")";
			}
			return result;
		}

		@SuppressLint("SwitchIntDef")
		private static String getType(@ConfigurationUIMode int uiMode) {
			switch (uiMode) {
				case Configuration.UI_MODE_TYPE_UNDEFINED:
					return "UI_MODE_TYPE_UNDEFINED";
				case Configuration.UI_MODE_TYPE_MASK:
					return "UI_MODE_TYPE_MASK";
				case Configuration.UI_MODE_TYPE_NORMAL:
					return "UI_MODE_TYPE_NORMAL";
				case Configuration.UI_MODE_TYPE_DESK:
					return "UI_MODE_TYPE_DESK(desk)";
				case Configuration.UI_MODE_TYPE_CAR:
					return "UI_MODE_TYPE_CAR(car)";
				case Configuration.UI_MODE_TYPE_TELEVISION:
					return "UI_MODE_TYPE_TELEVISION(television)";
				case Configuration.UI_MODE_TYPE_APPLIANCE:
					return "UI_MODE_TYPE_APPLIANCE(appliance)";
				case Configuration.UI_MODE_TYPE_WATCH:
					return "UI_MODE_TYPE_WATCH(watch)";
			}
			return "type::" + uiMode;
		}

		@SuppressLint("SwitchIntDef")
		private static String getNight(@ConfigurationUIMode int uiMode) {
			switch (uiMode) {
				case Configuration.UI_MODE_NIGHT_MASK:
					return "UI_MODE_NIGHT_MASK";
				case Configuration.UI_MODE_NIGHT_UNDEFINED:
					return "UI_MODE_NIGHT_UNDEFINED";
				case Configuration.UI_MODE_NIGHT_NO:
					return "UI_MODE_NIGHT_NO(notnight)";
				case Configuration.UI_MODE_NIGHT_YES:
					return "UI_MODE_NIGHT_YES(night)";
			}
			return "night::" + uiMode;
		}
	}
}
