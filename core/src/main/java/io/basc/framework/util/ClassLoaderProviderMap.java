package io.basc.framework.util;

import java.util.Comparator;
import java.util.TreeMap;

public final class ClassLoaderProviderMap<V extends ClassLoaderProvider> extends TreeMap<ClassLoader, V> {
	private static final long serialVersionUID = 1L;
	private final V defaultClassLoaderProvider;

	private static final Comparator<ClassLoader> CLASS_LOADER_COMPARATOR = (c1, c2) -> {
		return 0;
	};

	public ClassLoaderProviderMap(V defaultClassLoaderProvider) {
		super(CLASS_LOADER_COMPARATOR);
		this.defaultClassLoaderProvider = defaultClassLoaderProvider;
	}

	public V getDefaultClassLoaderProvider() {
		return defaultClassLoaderProvider;
	}

	public V getClassLoaderProvider(ClassLoader classLoader) {
		if (CLASS_LOADER_COMPARATOR.compare(classLoader, defaultClassLoaderProvider.getClassLoader()) > 0) {
			return defaultClassLoaderProvider;
		}

		// TODO 还未处理
		return null;
	}
}
