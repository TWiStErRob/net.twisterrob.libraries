package net.twisterrob.java.utils.tostring;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.zip.ZipEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.twisterrob.java.utils.tostring.stringers.DefaultStringer;
import net.twisterrob.java.utils.tostring.stringers.NullStringer;
import net.twisterrob.java.utils.tostring.stringers.NullTypeStringer;
import net.twisterrob.java.utils.tostring.stringers.StringStringer;
import net.twisterrob.java.utils.tostring.stringers.ZipEntryStringer;

public class StringerRepo {
	private static final Logger LOG = LoggerFactory.getLogger(StringerRepo.class);
	public static final StringerRepo INSTANCE = new StringerRepo();

	/** Added just so it can be optimized away from BaseApp */
	public static StringerRepo getInstance() {
		return INSTANCE;
	}

	private final Map<Class<?>, Stringer<?>> stringers = new HashMap<>();
	private final Map<Class<?>, Stringer<?>> cache = new HashMap<>();
	private Stringer<Object> defaultStringer = DefaultStringer.INSTANCE;

	protected StringerRepo() {
		initDefaults();
	}

	/** Visible for testing only. */
	public void initDefaults() {
		register(String.class, new StringStringer());
		register(ZipEntry.class, new ZipEntryStringer());
	}
	/** Visible for testing only. */
	public void clear() {
		stringers.clear();
		cache.clear();
	}

	public @Nonnull Stringer<Object> getDefault() {
		return defaultStringer;
	}
	public void setDefault(@Nonnull Stringer<Object> stringer) {
		defaultStringer = stringer;
	}

	@SuppressWarnings("unchecked")
	public <T> void register(@Nonnull String className, @Nonnull Stringer<?> stringer) {
		try {
			Class<T> clazz = (Class<T>)Class.forName(className);
			Stringer<? super T> typedStringer = (Stringer<? super T>)stringer;
			register(clazz, typedStringer);
		} catch (ClassNotFoundException ex) {
			String message = String.format(Locale.ROOT, "Cannot find class %s to register %s", className, stringer);
			LOG.error(message, ex);
			throw new IllegalArgumentException(message, ex);
		}
	}

	public <T> void register(@Nonnull Class<T> clazz, @Nonnull Stringer<? super T> stringer) {
		//noinspection ConstantConditions
		if (stringer == null) {
			throw new IllegalArgumentException("Stringer must not be null.");
		}
		cache.clear(); // invalidate everything, because we don't know what the subclasses of clazz are
		stringers.put(clazz, stringer);
	}

	public @Nonnull <T> Stringer<? super T> findByValue(@Nullable T value) {
		if (value == null) {
			return NullStringer.INSTANCE;
		}
		@SuppressWarnings("unchecked") Class<T> clazz = (Class<T>)value.getClass();
		return find(clazz);
	}

	@SuppressWarnings("unchecked")
	public @Nonnull <T> Stringer<? super T> find(@Nullable Class<T> clazz) {
		if (clazz == null) {
			return NullTypeStringer.INSTANCE;
		}

		Stringer<?> toString = cache.get(clazz);
		if (toString == null) {
			toString = findForSuperClasses(clazz);
		}
		if (toString == null) {
			toString = findForSuperInterfaces(clazz);
		}
		if (toString == null) {
			toString = defaultStringer;
		}
		cache.put(clazz, toString);
		return (Stringer<? super T>)toString;
	}

	private <T> Stringer<?> findForSuperClasses(@Nonnull Class<T> clazz) {
		Class<?> superClass = clazz;
		do {
			Stringer<?> toString = stringers.get(superClass);
			if (toString != null) {
				return toString;
			}
			superClass = superClass.getSuperclass();
		} while (superClass != null);
		return null;
	}

	private <T> Stringer<?> findForSuperInterfaces(@Nonnull Class<T> clazz) {
		Queue<Class<?>> queue = new ArrayDeque<>();
		Set<Class<?>> done = new HashSet<>();
		Class<?> superClass = clazz;
		do {
			// start with current class's interfaces
			for (Class<?> iface : superClass.getInterfaces()) {
				if (!done.contains(iface)) {
					queue.add(iface);
				}
			}
			while (!queue.isEmpty()) {
				Class<?> iface = queue.poll();
				Stringer<?> toString = stringers.get(iface);
				if (toString != null) {
					return toString;
				}
				// extend search to interfaces the current interface extends
				for (Class<?> superIface : iface.getInterfaces()) {
					if (done.add(superIface)) {
						queue.add(superIface);
					}
				}
			}
			superClass = superClass.getSuperclass();
		} while (superClass != null);
		return null;
	}
}
