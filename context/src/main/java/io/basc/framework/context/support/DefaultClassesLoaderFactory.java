package io.basc.framework.context.support;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.DefaultClassLoaderProvider;
import io.basc.framework.util.StringUtils;

public class DefaultClassesLoaderFactory implements ClassesLoaderFactory {
	private final ResourcePatternResolver resourcePatternResolver;
	private final ClassLoaderProvider classLoaderProvider;

	public DefaultClassesLoaderFactory() {
		this(new PathMatchingResourcePatternResolver(), null);
	}

	public DefaultClassesLoaderFactory(ClassLoader classLoader) {
		this(new PathMatchingResourcePatternResolver(classLoader), new DefaultClassLoaderProvider(classLoader));
	}

	public DefaultClassesLoaderFactory(ResourceLoader resourceLoader) {
		this(new PathMatchingResourcePatternResolver(resourceLoader), resourceLoader);
	}

	public DefaultClassesLoaderFactory(ResourcePatternResolver resourcePatternResolver,
			@Nullable ClassLoaderProvider classLoaderProvider) {
		this.resourcePatternResolver = resourcePatternResolver;
		this.classLoaderProvider = classLoaderProvider;
	}

	public ClassesLoader getClassesLoader(final String packageName, TypeFilter typeFilter) {
		String[] packageNames = StringUtils.splitToArray(packageName);
		DefaultClassesLoader editableClassesLoader = new DefaultClassesLoader();
		for (String name : packageNames) {
			editableClassesLoader.add(new PackageClassesLoader(resourcePatternResolver, name, this, typeFilter));
		}
		return editableClassesLoader;
	}

	public ClassLoader getClassLoader() {
		return ClassUtils.getClassLoader(classLoaderProvider);
	}
}
