package scw.util;

import scw.core.utils.ClassUtils;
import scw.lang.Nullable;

public final class DefaultClassLoaderProvider implements ClassLoaderProvider {
	private final Supplier<? extends ClassLoader> classLoader;
	
	public DefaultClassLoaderProvider(@Nullable Supplier<? extends ClassLoader> classLoader) {
		this.classLoader = classLoader;
	}

	public DefaultClassLoaderProvider(@Nullable ClassLoader classLoader) {
		this(new StaticSupplier<ClassLoader>(classLoader));
	}
	
	public DefaultClassLoaderProvider(final Class<?> clazz) {
		this(new Supplier<ClassLoader>() {
			public ClassLoader get() {
				return clazz.getClassLoader();
			}
		});
	}

	public ClassLoader getClassLoader() {
		return classLoader == null ? ClassUtils.getDefaultClassLoader()
				: classLoader.get();
	}
}
