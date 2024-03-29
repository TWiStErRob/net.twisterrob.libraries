package net.twisterrob.android.utils.tools;

import java.util.Locale;

import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import net.twisterrob.android.annotation.IntentFlags;
import net.twisterrob.android.annotation.TrimMemoryLevel;
import net.twisterrob.android.annotation.WindowFeature;
import net.twisterrob.android.utils.tostring.stringers.name.FragmentNameStringer;
import net.twisterrob.android.utils.tostring.stringers.name.ResourceNameStringer;
import net.twisterrob.java.annotations.DebugHelper;
import net.twisterrob.java.utils.StringTools;
import net.twisterrob.java.utils.tostring.StringerRepo;
import net.twisterrob.java.utils.tostring.ToStringer;
import net.twisterrob.java.utils.tostring.stringers.DefaultNameStringer;

public class StringerTools {
	@DebugHelper
	public static @NonNull <T> String toShortString(T value) {
		return new ToStringer(StringerRepo.INSTANCE, value, false).toString();
	}
	@DebugHelper
	public static @NonNull <T> String toString(T value) {
		return new ToStringer(StringerRepo.INSTANCE, value, true).toString();
	}
	/** @see ComponentCallbacks2 */
	@DebugHelper
	public static String toTrimMemoryString(@TrimMemoryLevel int level) {
		return TrimMemoryLevel.Converter.toString(level);
	}
	/** @see Intent#FLAG_* */
	@SuppressWarnings("JavadocReference")
	@DebugHelper
	public static String toActivityIntentFlagString(@IntentFlags int flags) {
		return IntentFlags.Converter.toString(flags, true);
	}
	public static String toDrawerLayoutStateString(int state) {
		switch (state) {
			case DrawerLayout.STATE_IDLE:
				return "STATE_IDLE";
			case DrawerLayout.STATE_DRAGGING:
				return "STATE_DRAGGING";
			case DrawerLayout.STATE_SETTLING:
				return "STATE_SETTLING";
			default:
				return "state::" + state;
		}
	}
	public static String toColorString(int color) {
		return String.format(Locale.ROOT, "#%02X%02X%02X%02X",
				Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color));
	}
	@DebugHelper
	public static String toFeatureString(@WindowFeature int featureId) {
		return WindowFeature.Converter.toString(featureId);
	}
	@DebugHelper
	public static String toNameString(Context context, @IdRes int id) {
		return new ToStringer(StringerRepo.INSTANCE, id, ResourceNameStringer.INSTANCE).toString();
	}
	@DebugHelper
	public static String toNameString(Fragment fragment) {
		return new ToStringer(StringerRepo.INSTANCE, fragment, FragmentNameStringer.INSTANCE).toString();
	}
	@DebugHelper
	public static String toNameString(Activity activity) {
		return new ToStringer(StringerRepo.INSTANCE, activity, DefaultNameStringer.INSTANCE).toString();
	}
	@DebugHelper
	public static String toNameString(Object object) {
		return new ToStringer(StringerRepo.INSTANCE, object, DefaultNameStringer.INSTANCE).toString();
	}
	public static String hashString(Object object) {
		return StringTools.hashString(object);
	}
}
