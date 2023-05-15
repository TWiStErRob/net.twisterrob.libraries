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

@IntDef(value = {
		Service.START_STICKY_COMPATIBILITY,
		Service.START_STICKY,
		Service.START_NOT_STICKY,
		Service.START_REDELIVER_INTENT,
})
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface ServiceStartResult {
	class Converter {
		/**
		 * @see Service#onStartCommand(Intent, int, int) return value
		 * @see Service#START_CONTINUATION_MASK
		 */
		@DebugHelper
		public static @NonNull String toString(@ServiceStartResult int flag) {
			switch (flag) {
				case Service.START_STICKY_COMPATIBILITY:
					return "START_STICKY_COMPATIBILITY";
				case Service.START_STICKY:
					return "START_STICKY";
				case Service.START_NOT_STICKY:
					return "START_NOT_STICKY";
				case Service.START_REDELIVER_INTENT:
					return "START_REDELIVER_INTENT";
				default:
					return "Unknown flags: 0x" + Integer.toHexString(flag);
			}
		}
	}
}
