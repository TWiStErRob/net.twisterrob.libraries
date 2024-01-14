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

import androidx.annotation.IntDef;

import net.twisterrob.java.annotations.DebugHelper;

@IntDef(value = {
		Configuration.KEYBOARDHIDDEN_UNDEFINED,
		Configuration.KEYBOARDHIDDEN_YES,
		Configuration.KEYBOARDHIDDEN_NO
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface ConfigurationKeyboardHidden {
	class Converter {
		@SuppressLint("SwitchIntDef")
		@DebugHelper
		public static String toString(@ConfigurationKeyboardHidden int kbdHidden) {
			switch (kbdHidden) {
				case Configuration.KEYBOARDHIDDEN_UNDEFINED:
					return "KEYBOARDHIDDEN_UNDEFINED";
				case Configuration.KEYBOARDHIDDEN_YES:
					return "KEYBOARDHIDDEN_YES(keyshidden)";
				case Configuration.KEYBOARDHIDDEN_NO:
					return "KEYBOARDHIDDEN_NO(keysexposed)";
				case 3: // @hide Configuration.KEYBOARDHIDDEN_SOFT
					return "KEYBOARDHIDDEN_SOFT(keyssoft)";
			}
			return "keyboardHidden::" + kbdHidden;
		}
	}
}
