package run.soeasy.framework.core;

import run.soeasy.framework.core.type.ClassUtils;

public interface ClassLoaderAccessor extends ClassLoaderProvider {

	ClassLoaderProvider getClassLoaderProvider();

	void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider);

	default void setClassLoader(ClassLoader classLoader) {
		setClassLoaderProvider(classLoader == null ? null : (() -> classLoader));
	}

	default void setClassLoader(Class<?> clazz) {
		setClassLoaderProvider(clazz == null ? null : (() -> clazz.getClassLoader()));
	}

	@Override
	default ClassLoader getClassLoader() {
		ClassLoaderProvider classLoaderProvider = getClassLoaderProvider();
		return classLoaderProvider == null ? ClassUtils.getDefaultClassLoader() : classLoaderProvider.getClassLoader();
	}
}
