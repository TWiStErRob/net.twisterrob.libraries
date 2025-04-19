package net.twisterrob.android.utils.tostring.stringers.detailed;

import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import androidx.annotation.NonNull;

import net.twisterrob.android.annotation.ConfigurationDensityDpi;
import net.twisterrob.android.annotation.ConfigurationHardKeyboardHidden;
import net.twisterrob.android.annotation.ConfigurationKeyboard;
import net.twisterrob.android.annotation.ConfigurationKeyboardHidden;
import net.twisterrob.android.annotation.ConfigurationNavigation;
import net.twisterrob.android.annotation.ConfigurationNavigationHidden;
import net.twisterrob.android.annotation.ConfigurationOrientation;
import net.twisterrob.android.annotation.ConfigurationScreenLayout;
import net.twisterrob.android.annotation.ConfigurationTouchscreen;
import net.twisterrob.android.annotation.ConfigurationUIMode;
import net.twisterrob.android.annotation.ViewLayoutDirection;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

/**
 * Supports features up to {@link VERSION_CODES#N}.
 * @see <a href="https://developer.android.com/guide/topics/resources/providing-resources.html#table2">
 *     API Guides > App Resources > Providing Resources > Configuration qualifier names</a>
 */
public class ConfigurationStringer extends Stringer<Configuration> {
	@SuppressWarnings("deprecation")
	@Override public void toString(@NonNull ToStringAppender append, Configuration config) {
		append.beginPropertyGroup("screen");
		append.rawProperty("orientation", ConfigurationOrientation.Converter.toString(config.orientation));
		append.rawProperty("touchScreen", ConfigurationTouchscreen.Converter.toString(config.touchscreen));
		append.rawProperty("layout", ConfigurationScreenLayout.Converter.toString(config.screenLayout));
		if (VERSION_CODES.M <= VERSION.SDK_INT) {
			append.booleanProperty(config.isScreenRound(), "round");
		}
		if (VERSION_CODES.JELLY_BEAN_MR1 <= VERSION.SDK_INT) {
			append.rawProperty("density", ConfigurationDensityDpi.Converter.toString(config.densityDpi));
		}
		if (VERSION_CODES.HONEYCOMB_MR2 <= VERSION.SDK_INT) {
			if (config.screenWidthDp == Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
				append.rawProperty("width", "SCREEN_WIDTH_DP_UNDEFINED");
			} else {
				append.measuredProperty("width", "dp", config.screenWidthDp);
			}
			if (config.screenHeightDp == Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
				append.rawProperty("height", "SCREEN_HEIGHT_DP_UNDEFINED");
			} else {
				append.measuredProperty("height", "dp", config.screenHeightDp);
			}
			if (config.smallestScreenWidthDp == Configuration.SMALLEST_SCREEN_WIDTH_DP_UNDEFINED) {
				append.rawProperty("smallestWidth", "SMALLEST_SCREEN_WIDTH_DP_UNDEFINED");
			} else {
				append.measuredProperty("smallestWidth", "dp", config.smallestScreenWidthDp);
			}
		}
		append.complexProperty("fontScale", config.fontScale);
		append.endPropertyGroup();

		append.beginPropertyGroup("layout");
		if (VERSION_CODES.JELLY_BEAN_MR1 <= VERSION.SDK_INT) {
			//noinspection WrongConstant
			append.rawProperty("direction", ViewLayoutDirection.Converter.toString(config.getLayoutDirection()));
		}
		append.rawProperty("uiMode", ConfigurationUIMode.Converter.toString(config.uiMode));
		append.endPropertyGroup();

		append.beginPropertyGroup("local");
		append.complexProperty("locale", config.locale);
		if (VERSION_CODES.N <= VERSION.SDK_INT) {
			append.complexProperty("locales", config.getLocales());
		}
		if (config.mnc == Configuration.MNC_ZERO) {
			append.rawProperty("MNC(mobile Network code)", "MNC_ZERO");
		} else {
			append.rawProperty("MNC(mobile Network code)", config.mnc);
		}
		if (config.mcc == 0) {
			append.rawProperty("MCC(mobile Country code)", "undefined");
		} else {
			append.rawProperty("MCC(mobile Country code)", config.mcc);
		}
		append.endPropertyGroup();

		append.beginPropertyGroup("keyboard");
		append.selfDescribingProperty(ConfigurationKeyboard.Converter.toString(config.keyboard));
		append.rawProperty("hidden", ConfigurationKeyboardHidden.Converter.toString(config.keyboardHidden));
		append.rawProperty("hardHidden", ConfigurationHardKeyboardHidden.Converter.toString(config.hardKeyboardHidden));
		append.endPropertyGroup();

		append.beginPropertyGroup("navigation");
		append.selfDescribingProperty(ConfigurationNavigation.Converter.toString(config.navigation));
		append.rawProperty("hidden", ConfigurationNavigationHidden.Converter.toString(config.navigationHidden));
		append.endPropertyGroup();
	}
}

