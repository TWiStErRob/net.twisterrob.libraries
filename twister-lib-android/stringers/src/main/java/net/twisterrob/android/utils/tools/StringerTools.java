package net.twisterrob.android.utils.tools;

import java.util.Locale;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.*;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import net.twisterrob.android.annotation.*;
import net.twisterrob.android.utils.tostring.stringers.name.*;
import net.twisterrob.java.annotations.DebugHelper;
import net.twisterrob.java.utils.StringTools;
import net.twisterrob.java.utils.tostring.*;
import net.twisterrob.java.utils.tostring.stringers.DefaultNameStringer;

public class StringerTools {

	@DebugHelper
	public static @NonNull <T> String toShortString(T value) {
		/*
			StrictMode policy violation: android.os.strictmode.NonSdkApiUsedViolation:
			Landroid/app/FragmentManagerState;->mActive:[Landroid/app/FragmentState;
			at net.twisterrob.java.utils.ReflectionTools.findDeclaredField(ReflectionTools.java:92)
			at net.twisterrob.java.utils.ReflectionTools.get(ReflectionTools.java:60)
			at android.app.FragmentManagerStateStringer.toString(FragmentManagerStateStringer.java:27)
			at net.twisterrob.android.utils.tools.StringerTools.toString(StringerTools.java:27)
			net.twisterrob.android.utils.log.LoggingActivity.log(LoggingActivity.java:439)
		 */
		StrictMode.VmPolicy originalPolicy = StrictMode.getVmPolicy();
		if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(originalPolicy)
					.permitNonSdkApiUsage().build());
		}
		try {
			return new ToStringer(StringerRepo.INSTANCE, value, false).toString();
		} finally {
			StrictMode.setVmPolicy(originalPolicy);
		}
	}

	@DebugHelper
	public static @NonNull <T> String toString(T value) {
		/*
			StrictMode policy violation: android.os.strictmode.NonSdkApiUsedViolation:
			Landroid/app/FragmentManagerState;->mActive:[Landroid/app/FragmentState;
			at net.twisterrob.java.utils.ReflectionTools.findDeclaredField(ReflectionTools.java:92)
			at net.twisterrob.java.utils.ReflectionTools.get(ReflectionTools.java:60)
			at android.app.FragmentManagerStateStringer.toString(FragmentManagerStateStringer.java:27)
			at net.twisterrob.android.utils.tools.StringerTools.toString(StringerTools.java:27)
			net.twisterrob.android.utils.log.LoggingActivity.log(LoggingActivity.java:439)
		 */
		StrictMode.VmPolicy originalPolicy = StrictMode.getVmPolicy();
		if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(originalPolicy)
					.permitNonSdkApiUsage().build());
		}
		try {
			return new ToStringer(StringerRepo.INSTANCE, value, true).toString();
		} finally {
			StrictMode.setVmPolicy(originalPolicy);
		}
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
