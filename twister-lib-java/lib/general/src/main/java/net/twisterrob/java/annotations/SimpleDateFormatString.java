package net.twisterrob.java.annotations;

import java.lang.annotation.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.annotation.*;
import javax.annotation.meta.*;

/**
 * This qualifier is used to denote String values that should be a format strings accepted by {@link SimpleDateFormatString}.
 */
@Documented
@Syntax("SimpleDateFormat")
@TypeQualifierNickname
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleDateFormatString {
	@SuppressWarnings("NoHardKeywords") // Not used in Kotlin yet.
	When when() default When.ALWAYS;

	class Checker implements TypeQualifierValidator<SimpleDateFormatString> {
		public @Nonnull When forConstantValue(@Nonnull SimpleDateFormatString annotation, Object value) {
			if (!(value instanceof String)) {
				return When.NEVER;
			}

			try {
				@SuppressWarnings("unused")
				SimpleDateFormat testFormatByParsing = new java.text.SimpleDateFormat((String)value, Locale.ROOT);
			} catch (IllegalArgumentException e) {
				return When.NEVER;
			}
			return When.ALWAYS;
		}
	}
}
