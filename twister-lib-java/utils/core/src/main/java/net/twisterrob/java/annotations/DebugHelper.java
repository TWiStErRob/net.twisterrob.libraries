package net.twisterrob.java.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Annotate methods that shouldn't be used in production,
 * and don't use anything annotated with this for anything else other than temporary development debugging or logging.
 * Needs to go to the class file so ProGuard can use it.
 */
@Retention(RetentionPolicy.CLASS)
@Target({CONSTRUCTOR, METHOD, TYPE, PACKAGE})
public @interface DebugHelper {
}
