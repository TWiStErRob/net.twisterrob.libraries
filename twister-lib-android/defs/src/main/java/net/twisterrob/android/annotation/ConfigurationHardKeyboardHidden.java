package net.twisterrob.android.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import android.content.res.Configuration;

import androidx.annotation.IntDef;

import net.twisterrob.java.annotations.DebugHelper;

@IntDef(value = {
		Configuration.HARDKEYBOARDHIDDEN_UNDEFINED,
		Configuration.HARDKEYBOARDHIDDEN_YES,
		Configuration.HARDKEYBOARDHIDDEN_NO
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface ConfigurationHardKeyboardHidden {
	class Converter {
		@DebugHelper
		public static String toString(@ConfigurationHardKeyboardHidden int hardKbdHidden) {
			switch (hardKbdHidden) {
				case Configuration.HARDKEYBOARDHIDDEN_UNDEFINED:
					return "HARDKEYBOARDHIDDEN_UNDEFINED";
				case Configuration.HARDKEYBOARDHIDDEN_YES:
					return "HARDKEYBOARDHIDDEN_YES";
				case Configuration.HARDKEYBOARDHIDDEN_NO:
					return "HARDKEYBOARDHIDDEN_NO";
			}
			return "hardKeyboardHidden::" + hardKbdHidden;
		}
	}
}
