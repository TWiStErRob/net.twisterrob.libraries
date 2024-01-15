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
		Configuration.NAVIGATIONHIDDEN_UNDEFINED,
		Configuration.NAVIGATIONHIDDEN_YES,
		Configuration.NAVIGATIONHIDDEN_NO
})
@Retention(RetentionPolicy.SOURCE)
@Target({FIELD, LOCAL_VARIABLE, PARAMETER, METHOD})
public @interface ConfigurationNavigationHidden {
	class Converter {
		@DebugHelper
		public static String toString(@ConfigurationNavigationHidden int navHidden) {
			switch (navHidden) {
				case Configuration.NAVIGATIONHIDDEN_UNDEFINED:
					return "NAVIGATIONHIDDEN_UNDEFINED";
				case Configuration.NAVIGATIONHIDDEN_YES:
					return "NAVIGATIONHIDDEN_YES(navhidden)";
				case Configuration.NAVIGATIONHIDDEN_NO:
					return "NAVIGATIONHIDDEN_NO(navexposed)";
			}
			return "navigationHidden::" + navHidden;
		}
	}
}
