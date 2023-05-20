package net.twisterrob.android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import android.app.Service;
import android.content.Intent;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import net.twisterrob.java.annotations.DebugHelper;

@IntDef(flag = true, value = {
		Service.START_FLAG_REDELIVERY,
		Service.START_FLAG_RETRY,
})
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface ServiceStartFlag {
	class Converter {
		/**
		 * @see Service#onStartCommand(Intent, int, int)  {@code flags} argument
		 */
		@DebugHelper
		public static @NonNull String toString(@ServiceStartFlag int flags) {
			StringBuilder sb = new StringBuilder();
			sb.append("0x").append(Integer.toHexString(flags));
			sb.append("[");

			int f = flags;
			f = handleFlag(sb, f, Service.START_FLAG_REDELIVERY, "START_FLAG_REDELIVERY");
			f = handleFlag(sb, f, Service.START_FLAG_RETRY, "START_FLAG_RETRY");

			if (f != 0) {
				sb.append(" and remainder: 0x").append(Integer.toHexString(f));
			}
			sb.append("]");
			return sb.toString();
		}

		private static @ServiceStartFlag int handleFlag(StringBuilder sb,
				@ServiceStartFlag int flags, @ServiceStartFlag int flag, String flagName) {
			if ((flags & flag) == flag) {
				flags &= ~flag;
				if (sb.charAt(sb.length() - 1) != '[') {
					sb.append(" | ");
				}
				sb.append(flagName);
			}
			return flags;
		}
	}
}
