package io.basc.framework.util;

public class DefaultClassLoaderProvider implements ConfigurableClassLoaderProvider {
	private ClassLoaderProvider classLoaderProvider;

	public DefaultClassLoaderProvider() {
	}

	public DefaultClassLoaderProvider(Class<?> clazz) {
		this.classLoaderProvider = clazz == null ? null : (() -> clazz.getClassLoader());
	}

	public DefaultClassLoaderProvider(ClassLoader classLoader) {
		this.classLoaderProvider = classLoader == null ? null : (() -> classLoader);
	}

	public DefaultClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
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
