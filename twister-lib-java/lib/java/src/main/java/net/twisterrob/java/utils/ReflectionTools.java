package net.twisterrob.java.utils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO refactor get/set to have try* variants
public class ReflectionTools {
	private static final Logger LOG = LoggerFactory.getLogger(ReflectionTools.class);

	@SuppressWarnings("unchecked")
	public static <T> T getStatic(@Nonnull Class<?> clazz, @Nonnull String fieldName) {
		try {
			Field field = findDeclaredField(clazz, fieldName);
			field.setAccessible(true);
			return (T)field.get(null);
		} catch (Exception ex) {
			LOG.warn("Cannot read static field {} of {}", fieldName, clazz, ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getStatic(@Nonnull String className, @Nonnull String fieldName) {
		try {
			return getStatic(Class.forName(className), fieldName);
		} catch (Exception ex) {
			LOG.warn("Cannot read static field {} of {}", fieldName, className, ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static void setStatic(@Nonnull Class<?> clazz, @Nonnull String fieldName, @Nullable Object value) {
		try {
			Field field = findDeclaredField(clazz, fieldName);
			field.setAccessible(true);
			field.set(null, value);
		} catch (Exception ex) {
			LOG.warn("Cannot write static field {} of {} with {}", fieldName, clazz, value, ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static void setStatic(@Nonnull String className, @Nonnull String fieldName, @Nullable Object value) {
		try {
			setStatic(Class.forName(className), fieldName, value);
		} catch (Exception ex) {
			LOG.warn("Cannot write static field {} of {} with {}", fieldName, className, value, ex);
		}
	}

	public static <T> T get(@Nonnull Object object, @Nonnull String fieldName) {
		try {
			Field field = findDeclaredField(object.getClass(), fieldName);
			return get(object, field);
		} catch (Exception ex) {
			//noinspection ConstantConditions prevent NPE when object is null, even though it was declared not null
			Class<?> clazz = object != null? object.getClass() : null;
			LOG.warn("Cannot read field {} of ({}){}", fieldName, clazz, object, ex);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(@Nonnull Object object, @Nonnull Field field) {
		try {
			field.setAccessible(true);
			return (T)field.get(object);
		} catch (Exception ex) {
			//noinspection ConstantConditions prevent NPE when object is null, even though it was declared not null
			Class<?> clazz = object != null? object.getClass() : null;
			LOG.warn("Cannot read field {} of ({}){}", field, clazz, object, ex);
		}
		return null;
	}

	public static void set(@Nonnull Object object, @Nonnull String fieldName, @Nullable Object value) {
		try {
			Field field = findDeclaredField(object.getClass(), fieldName);
			set(object, field, value);
		} catch (Exception ex) {
			//noinspection ConstantConditions prevent NPE when object is null, even though it was declared not null
			Class<?> clazz = object != null? object.getClass() : null;
			LOG.warn("Cannot write field {} of ({}){}", fieldName, clazz, object, ex);
		}
	}

	public static void set(@Nonnull Object object, @Nonnull Field field, @Nullable Object value) {
		try {
			field.setAccessible(true);
			field.set(object, value);
		} catch (Exception ex) {
			//noinspection ConstantConditions prevent NPE when object is null, even though it was declared not null
			Class<?> clazz = object != null? object.getClass() : null;
			LOG.warn("Cannot write field {} of ({}){}", field, clazz, object, ex);
		}
	}

	/**
	 * Like {@link Class#getDeclaredField}, but looking in all superclasses as well.
	 * @throws NoSuchFieldException if a field with the specified name is not found in the class hierarchy.
	 * @see Class#getDeclaredField(String)
	 */
	public static @Nonnull Field findDeclaredField(@Nonnull Class<?> clazz, @Nonnull String fieldName)
			throws NoSuchFieldException {
		do {
			try {
				return clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ex) {
				clazz = clazz.getSuperclass();
			}
		} while (clazz != null);
		throw new NoSuchFieldException(fieldName);
	}

	public static @Nullable Field tryFindDeclaredField(@Nonnull Class<?> clazz, @Nonnull String fieldName) {
		try {
			return findDeclaredField(clazz, fieldName);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	public static boolean instanceOf(String clazz, Object value) {
		try {
			return Class.forName(clazz).isInstance(value);
		} catch (ClassNotFoundException ex) {
			LOG.warn("Cannot find class {} to check instanceof {}", clazz, value, ex);
			return false;
		}
	}

	/**
	 * Like {@link Class#getDeclaredMethod}, but looking in all superclasses as well.
	 * @throws NoSuchMethodException if a method with the specified name is not found in the class hierarchy.
	 * @see Class#getDeclaredMethod(String, Class[])
	 */
	public static @Nonnull Method findDeclaredMethod(@Nonnull Class<?> clazz,
			@Nonnull String methodName, @Nonnull Class<?>... parameterTypes) throws NoSuchMethodException {
		Class<?> current = clazz;
		do {
			try {
				return current.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException ex) {
				current = current.getSuperclass();
			}
		} while (current != null);
		List<Class<?>> params = Arrays.asList(parameterTypes);
		throw new NoSuchMethodException("Cannot find " + methodName + params + " on " + clazz);
	}

	public static @Nullable Method tryFindDeclaredMethod(@Nonnull Class<?> clazz,
			@Nonnull String methodName, @Nonnull Class<?>... parameterTypes) {
		try {
			return findDeclaredMethod(clazz, methodName, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	public static <T extends AccessibleObject> T trySetAccessible(T reflected) {
		try {
			ensureAccessible(reflected);
			return reflected;
		} catch (Exception ex) {
			return null;
		}
	}

	public static <T extends AccessibleObject> T ensureAccessible(T reflected) {
		if (reflected != null) {
			reflected.setAccessible(true);
		}
		return reflected;
	}

	public static @Nullable Field tryFindConstant(@Nonnull Class<?> clazz, @Nonnull Object value) {
		try {
			return findConstant(clazz, value);
		} catch (IllegalAccessException e) {
			return null;
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	public static @Nonnull Field findConstant(@Nonnull Class<?> clazz, @Nonnull Object value)
			throws IllegalAccessException, NoSuchFieldException {
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			if (isStatic(field.getModifiers()) && value.equals(field.get(null))) {
				return field;
			}
		}
		throw new NoSuchFieldException("Cannot find field on " + clazz + " with value: " + value);
	}

	public static @Nonnull Class<?> forName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Reset a {@link Throwable}'s cause to itself.
	 * This will trigger the ability to call {@link Throwable#initCause(Throwable)}.
	 * This is necessary because some subclasses of {@link Throwable} (e.g. {@code NoMatchingViewException})
	 * call {@link Throwable#Throwable(String, Throwable)} with a {@code null} cause.
	 *
	 * @see <a href="https://github.com/ota4j-team/opentest4j/issues/70">StackOverflow</a>
	 */
	public static void enableInitCause(Throwable ex) {
		if (ex.getCause() != null) {
			throw new IllegalArgumentException(
					"Cannot enable initCause() on a Throwable with a cause", ex);
		}
		clearCause(ex);
	}

	public static Throwable clearCause(Throwable exception) {
		set(exception, "cause", exception);
		return exception;
	}

	public static @Nullable Class<?> tryForName(@Nonnegative String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException ex) {
			return null;
		}
	}
}
