package net.twisterrob.android.app;

import java.util.*;

import net.twisterrob.android.utils.cache.*;

public class AppCaches {
	public static final String CACHE_IMAGE = ImageSDNetCache.class.getName();
	private final Map<String, Cache<?, ?>> m_caches = new HashMap<String, Cache<?, ?>>();

	public <K, V> Cache<K, V> getCache(final String cacheName) {
		Cache<K, V> cache = (Cache<K, V>)m_caches.get(cacheName);
		if (cache == null) {
			cache = createCache(cacheName);
			m_caches.put(cacheName, cache);
		}
		return cache;
	}

	private static <T> T createCache(final String cacheClass) {
		try {
			return (T)Class.forName(cacheClass).newInstance();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}