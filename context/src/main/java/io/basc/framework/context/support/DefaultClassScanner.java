package io.basc.framework.context.support;

import java.util.Arrays;

import io.basc.framework.context.ConfigurableClassScanner;
import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.PackageClassesLoader;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ConfigurableClassLoaderProvider;
import io.basc.framework.util.DefaultServiceLoader;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.StringUtils;

public class DefaultClassScanner extends ConfigurableClassScanner implements ConfigurableClassLoaderProvider {
	private volatile MetadataReaderFactory metadataReaderFactory;
	private volatile ResourcePatternResolver resourcePatternResolver;
	private ClassLoaderProvider classLoaderProvider;

	public ClassLoaderProvider getClassLoaderProvider() {
		return classLoaderProvider;
	}

	public void setClassLoaderProvider(ClassLoaderProvider classLoaderProvider) {
		this.classLoaderProvider = classLoaderProvider;
	}

	@Override
	public boolean canScan(String pattern) {
		if (super.canScan(pattern)) {
			return true;
		}

		String[] packageNames = StringUtils.splitToArray(pattern);
		return CollectionUtils.isAny(Arrays.asList(packageNames), (e) -> StringUtils.verifyPackageName(e));
	}

	@Override
	public ServiceLoader<Class<?>> scan(String pattern, TypeFilter filter) {
		if (super.canScan(pattern)) {
			return super.scan(pattern, filter);
		}

		String[] packageNames = StringUtils.splitToArray(pattern);
		DefaultServiceLoader<Class<?>> editableClassesLoader = new DefaultServiceLoader<>();
		for (String name : packageNames) {
			if (!StringUtils.verifyPackageName(name)) {
				continue;
			}
			PackageClassesLoader packageClassesLoader = new PackageClassesLoader(getResourcePatternResolver(), name);
			packageClassesLoader.setTypeFilter(filter);
			packageClassesLoader.setMetadataReaderFactory(getMetadataReaderFactory());
			editableClassesLoader.registerLoader(packageClassesLoader);
		}
		return editableClassesLoader.isEmpty() ? ServiceLoader.empty() : editableClassesLoader;
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
