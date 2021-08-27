package io.basc.framework.util;

import io.basc.framework.core.utils.ClassUtils;

public class ClassLoaderAccessor implements ClassLoaderProvider {
	private ClassLoaderProvider classLoaderProvider;

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	public void setClassLoader(ClassLoader classLoader) {
		setClassLoaderProvider(new DefaultClassLoaderProvider(classLoader));
	}

	@Override
	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}
}
