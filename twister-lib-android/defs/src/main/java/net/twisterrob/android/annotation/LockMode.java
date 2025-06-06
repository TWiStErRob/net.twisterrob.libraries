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

import androidx.annotation.IntDef;
import androidx.drawerlayout.widget.DrawerLayout;

import net.twisterrob.java.annotations.DebugHelper;

@SuppressLint("UseRequiresApi")
// It is revised for L or below only. Newer features are not supported yet.
@TargetApi(VERSION_CODES.LOLLIPOP)
@IntDef(value = {
		DrawerLayout.LOCK_MODE_UNDEFINED,
		DrawerLayout.LOCK_MODE_UNLOCKED,
		DrawerLayout.LOCK_MODE_LOCKED_OPEN,
		DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface LockMode {
	class Converter {
		@DebugHelper
		public static String toString(@LockMode int lockMode) {
			switch (lockMode) {
				case DrawerLayout.LOCK_MODE_UNDEFINED:
					return "LOCK_MODE_UNDEFINED";
				case DrawerLayout.LOCK_MODE_UNLOCKED:
					return "LOCK_MODE_UNLOCKED";
				case DrawerLayout.LOCK_MODE_LOCKED_OPEN:
					return "LOCK_MODE_LOCKED_OPEN";
				case DrawerLayout.LOCK_MODE_LOCKED_CLOSED:
					return "LOCK_MODE_LOCKED_CLOSED";
			}
			return "lockMode::" + lockMode;
		}
	}
}
