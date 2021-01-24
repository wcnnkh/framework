package scw.util;

import scw.core.utils.ClassUtils;
import scw.lang.Nullable;

public final class DefaultClassLoaderProvider implements ClassLoaderProvider {
	private final ClassLoader classLoader;

	public DefaultClassLoaderProvider(@Nullable ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public DefaultClassLoaderProvider(Class<?> clazz) {
		this(clazz.getClassLoader());
	}

	public ClassLoader getClassLoader() {
		return classLoader == null ? ClassUtils.getDefaultClassLoader()
				: classLoader;
	}
}
