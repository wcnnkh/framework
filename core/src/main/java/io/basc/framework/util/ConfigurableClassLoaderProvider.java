package io.basc.framework.util;

public interface ConfigurableClassLoaderProvider extends ClassLoaderProvider {

	void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider);

	default void setClassLoader(ClassLoader classLoader) {
		setClassLoaderProvider(classLoader == null ? null : (() -> classLoader));
	}

	default void setClassLoader(Class<?> clazz) {
		setClassLoaderProvider(clazz == null ? null : (() -> clazz.getClassLoader()));
	}

}
