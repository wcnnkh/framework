package io.basc.framework.context.support;

import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.PackageClassesLoader;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.util.DefaultClassLoaderProvider;
import io.basc.framework.util.DefaultServiceLoader;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.StringUtils;

public class DefaultClassesLoaderFactory extends DefaultClassLoaderProvider implements ClassesLoaderFactory {
	private volatile MetadataReaderFactory metadataReaderFactory;
	private volatile ResourcePatternResolver resourcePatternResolver;

	public ServiceLoader<Class<?>> getClassesLoader(final String packageName, TypeFilter typeFilter) {
		String[] packageNames = StringUtils.splitToArray(packageName);
		DefaultServiceLoader<Class<?>> editableClassesLoader = new DefaultServiceLoader<Class<?>>();
		for (String name : packageNames) {
			PackageClassesLoader packageClassesLoader = new PackageClassesLoader(getResourcePatternResolver(), name);
			packageClassesLoader.setTypeFilter(typeFilter);
			packageClassesLoader.setMetadataReaderFactory(getMetadataReaderFactory());
			editableClassesLoader.registerLoader(packageClassesLoader);
		}
		return editableClassesLoader;
	}

	public MetadataReaderFactory getMetadataReaderFactory() {
		if (metadataReaderFactory == null) {
			synchronized (this) {
				if (metadataReaderFactory == null) {
					metadataReaderFactory = new CachingMetadataReaderFactory(getResourcePatternResolver());
				}
			}
		}
		return metadataReaderFactory;
	}

	public ResourcePatternResolver getResourcePatternResolver() {
		if (resourcePatternResolver == null) {
			synchronized (this) {
				if (resourcePatternResolver == null) {
					resourcePatternResolver = new PathMatchingResourcePatternResolver(getClassLoader());
				}
			}
		}
		return resourcePatternResolver;
	}

	public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
		this.metadataReaderFactory = metadataReaderFactory;
	}

	public void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
		this.resourcePatternResolver = resourcePatternResolver;
	}
}
