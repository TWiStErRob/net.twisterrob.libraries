package net.twisterrob.android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import android.annotation.SuppressLint;
import android.app.ActivityManager;

import androidx.annotation.IntDef;

import net.twisterrob.java.annotations.DebugHelper;

@SuppressLint("InlinedApi")
@IntDef(value = {
		ActivityManager.LOCK_TASK_MODE_NONE,
		ActivityManager.LOCK_TASK_MODE_LOCKED,
		ActivityManager.LOCK_TASK_MODE_PINNED,
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface LockTaskMode {
	class Converter {
		@DebugHelper
		public static String toString(@LockTaskMode int mode) {
			switch (mode) {
				case ActivityManager.LOCK_TASK_MODE_NONE:
					return "LOCK_TASK_MODE_NONE";
				case ActivityManager.LOCK_TASK_MODE_LOCKED:
					return "LOCK_TASK_MODE_LOCKED";
				case ActivityManager.LOCK_TASK_MODE_PINNED:
					return "LOCK_TASK_MODE_PINNED";
			}
			return "lockTaskMode::" + mode;
		}
	}
}
