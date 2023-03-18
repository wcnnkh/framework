package io.basc.framework.context.support;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.DefaultClassLoaderProvider;
import io.basc.framework.util.StringUtils;

public class DefaultClassesLoaderFactory extends DefaultClassLoaderProvider implements ClassesLoaderFactory {
	private final ResourcePatternResolver resourcePatternResolver;

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
		super(classLoaderProvider);
		this.resourcePatternResolver = resourcePatternResolver;
	}

	public ClassesLoader getClassesLoader(final String packageName, TypeFilter typeFilter) {
		String[] packageNames = StringUtils.splitToArray(packageName);
		DefaultClassesLoader editableClassesLoader = new DefaultClassesLoader();
		for (String name : packageNames) {
			editableClassesLoader
					.registerLoader(new PackageClassesLoader(resourcePatternResolver, name, this, typeFilter));
		}
		return editableClassesLoader;
	}
}
