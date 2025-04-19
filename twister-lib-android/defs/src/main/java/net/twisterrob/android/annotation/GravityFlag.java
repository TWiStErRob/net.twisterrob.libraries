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
import android.view.Gravity;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.CLIP_HORIZONTAL;
import static android.view.Gravity.CLIP_VERTICAL;
import static android.view.Gravity.DISPLAY_CLIP_HORIZONTAL;
import static android.view.Gravity.DISPLAY_CLIP_VERTICAL;
import static android.view.Gravity.END;
import static android.view.Gravity.FILL;
import static android.view.Gravity.FILL_HORIZONTAL;
import static android.view.Gravity.FILL_VERTICAL;
import static android.view.Gravity.HORIZONTAL_GRAVITY_MASK;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.NO_GRAVITY;
import static android.view.Gravity.RIGHT;
import static android.view.Gravity.START;
import static android.view.Gravity.TOP;
import static android.view.Gravity.VERTICAL_GRAVITY_MASK;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;

import net.twisterrob.java.annotations.DebugHelper;
import net.twisterrob.java.utils.StringTools;

@SuppressLint("UseRequiresApi")
// It is revised for L or below only. Newer features are not supported yet.
@TargetApi(VERSION_CODES.LOLLIPOP)
@IntDef(flag = true, value = {
		Gravity.NO_GRAVITY,
		Gravity.TOP,
		Gravity.BOTTOM,
		Gravity.LEFT,
		Gravity.RIGHT,
		Gravity.CENTER_VERTICAL,
		Gravity.FILL_VERTICAL,
		Gravity.CENTER_HORIZONTAL,
		Gravity.FILL_HORIZONTAL,
		Gravity.CENTER,
		Gravity.FILL,
		Gravity.CLIP_VERTICAL,
		Gravity.CLIP_HORIZONTAL,
		Gravity.DISPLAY_CLIP_VERTICAL,
		Gravity.DISPLAY_CLIP_HORIZONTAL,
		Gravity.START,
		GravityCompat.START,
		Gravity.END,
		GravityCompat.END
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface GravityFlag {
	class Converter {
		static final String FLAG_LIST_START = "[";
		static final String FLAG_LIST_SEPARATOR = " | ";
		static final String FLAG_LIST_END = "]";
		/**
		 * Vertical pull first, then horizontal pull.
		 * Strive for the simplest possible form, so redundancy may give the same result.
		 */
		@DebugHelper
		@SuppressLint("WrongConstant") // REPORT FLAG_LIST_ false positives.
		public static @NonNull String toString(@GravityFlag int gravity) {
			StringBuilder sb = new StringBuilder();
			sb.append("0x").append(Integer.toHexString(gravity));
			sb.append(FLAG_LIST_START);

			if (gravity == NO_GRAVITY) {
				sb.append("NO_GRAVITY");
			} else {
				@GravityFlag int f = gravity;
				f = handleFlag(sb, f, FILL, "FILL");
				if ((f & (HORIZONTAL_GRAVITY_MASK | VERTICAL_GRAVITY_MASK)) == CENTER) {
					// only handle CENTER if that's the full value. 
					f = handleFlag(sb, f, CENTER, "CENTER");
				}
				f = handleVerticalFlags(sb, f);
				f = handleHorizontalFlags(sb, f);
				//noinspection WrongConstant handle relative case to prevent known remainder showing up 
				f = handleFlag(sb, f, Gravity.RELATIVE_LAYOUT_DIRECTION, "RELATIVE_LAYOUT_DIRECTION");
				if (f != 0) {
					sb.append(FLAG_LIST_SEPARATOR).append("remainder: 0x").append(Integer.toHexString(f));
				}
			}
			sb.append(FLAG_LIST_END);
			return sb.toString();
		}
		private static @GravityFlag int handleVerticalFlags(StringBuilder sb, @GravityFlag int f) {
			// more general to more specific order, because the general one may include bits from specific ones
			f = handleFlag(sb, f, FILL_VERTICAL, "FILL_VERTICAL");
			f = handleFlag(sb, f, TOP, "TOP");
			f = handleFlag(sb, f, BOTTOM, "BOTTOM");
			f = handleFlag(sb, f, CENTER_VERTICAL, "CENTER_VERTICAL");
			f = handleFlag(sb, f, CLIP_VERTICAL, "CLIP_VERTICAL");
			f = handleFlag(sb, f, DISPLAY_CLIP_VERTICAL, "DISPLAY_CLIP_VERTICAL");
			return f;
		}
		private static @GravityFlag int handleHorizontalFlags(StringBuilder sb, @GravityFlag int f) {
			// more general to more specific order, because the general one may include bits from specific ones
			f = handleFlag(sb, f, FILL_HORIZONTAL, "FILL_HORIZONTAL");
			f = handleFlag(sb, f, START, "START");
			f = handleFlag(sb, f, END, "END");
			f = handleFlag(sb, f, LEFT, "LEFT");
			f = handleFlag(sb, f, RIGHT, "RIGHT");
			f = handleFlag(sb, f, CENTER_HORIZONTAL, "CENTER_HORIZONTAL");
			f = handleFlag(sb, f, CLIP_HORIZONTAL, "CLIP_HORIZONTAL");
			f = handleFlag(sb, f, DISPLAY_CLIP_HORIZONTAL, "DISPLAY_CLIP_HORIZONTAL");
			return f;
		}

		@SuppressLint("WrongConstant") // REPORT FLAG_LIST_ false positives.
		private static @GravityFlag int handleFlag(StringBuilder sb,
				@GravityFlag int flags, @GravityFlag int flag, String flagName) {
			if ((flags & flag) == flag) {
				flags &= ~flag;
				if (!StringTools.endsWith(sb, FLAG_LIST_START)) {
					sb.append(FLAG_LIST_SEPARATOR);
				}
				sb.append(flagName);
			}
			return flags;
		}
	}
}
