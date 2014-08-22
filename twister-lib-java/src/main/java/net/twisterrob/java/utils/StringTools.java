package net.twisterrob.java.utils;

import java.io.IOException;
import java.net.*;
import java.util.*;

public final class StringTools {
	private StringTools() {
		// prevent instantiation
	}

	public static String format(final String messageFormat, final Object... formatArgs) {
		if (formatArgs == null || formatArgs.length == 0) {
			return messageFormat;
		}
		return String.format(messageFormat, formatArgs);
	}

	public static String join(final Iterable<?> list, final String separator) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> iterator = list.iterator();
		while (iterator.hasNext()) {
			sb.append(iterator.next());
			if (iterator.hasNext()) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	public static String[] toStringArray(Object... objectArr) {
		if (objectArr == null) {
			return null;
		}
		String[] stringArr = new String[objectArr.length];
		for (int i = 0; i < stringArr.length; ++i) {
			stringArr[i] = String.valueOf(objectArr[i]);
		}
		return stringArr;
	}

	public static URL createUrl(final String type, final String... urls) throws IOException {
		String url = CollectionTools.coalesce(urls);
		if (url != null) {
			try {
				return new URL(url);
			} catch (MalformedURLException ex) {
				throw new IOException(StringTools.format("Cannot associate %s Url: %s", type, url), ex);
			}
		}
		return null;
	}

	public static String toNullString(final Object o, final String nullString) {
		if (nullString == null) {
			return String.valueOf(o);
		}
		return o != null? o.toString() : nullString;
	}

	public static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty() || string.trim().isEmpty();
	}

	/** Inverse of {@link Locale#toString()}. */
	public static Locale toLocale(String localeToString) {
		if (localeToString == null) {
			return Locale.ROOT;
		}
		String[] parts = localeToString.split("_");
		switch (parts.length) {
			case 0:
				return Locale.ROOT;
			case 1:
				return new Locale(parts[0]);
			case 2:
				return new Locale(parts[0], parts[1]);
			case 3:
				return new Locale(parts[0], parts[1], parts[2]);
			default:
				throw new IllegalArgumentException("Invalid locale: " + localeToString);
		}
	}
}