package io.basc.framework.lang;

import io.basc.framework.util.ClassUtils;

public class DefaultClassLoaderAccessor implements ClassLoaderAccessor {
	private ClassLoaderProvider classLoaderProvider;

	public DefaultClassLoaderAccessor() {
	}

	public DefaultClassLoaderAccessor(Class<?> clazz) {
		this.classLoaderProvider = clazz == null ? null : (() -> clazz.getClassLoader());
	}

	public DefaultClassLoaderAccessor(ClassLoader classLoader) {
		this.classLoaderProvider = classLoader == null ? null : (() -> classLoader);
	}

	public DefaultClassLoaderAccessor(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public void setClassLoaderProvider(Class<?> clazz) {
		this.classLoaderProvider = clazz == null ? null : (() -> clazz.getClassLoader());
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoaderProvider = classLoader == null ? null : (() -> classLoader);
	}

	public ClassLoader getClassLoader() {
		return classLoaderProvider == null ? ClassUtils.getDefaultClassLoader() : classLoaderProvider.getClassLoader();
	}
}
