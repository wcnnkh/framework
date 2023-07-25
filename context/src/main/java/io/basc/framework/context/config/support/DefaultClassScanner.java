package io.basc.framework.context.config.support;

import java.util.Arrays;

import io.basc.framework.context.config.ConfigurableClassScanner;
import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.PackageClassesLoader;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.ClassLoaderProviderMap;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.spi.ServiceLoader;
import io.basc.framework.util.spi.ServiceLoaders;
import lombok.Data;
import lombok.RequiredArgsConstructor;

public class DefaultClassScanner extends ConfigurableClassScanner {
	private final ClassLoaderProviderMap<ResourcePatternResolver> resourcePatternResolverMap = new ClassLoaderProviderMap<>(
			new PathMatchingResourcePatternResolver());
	private final ClassLoaderProviderMap<MetadataReaderFactoryCache> metadataReaderFactoryMap = new ClassLoaderProviderMap<>(
			new MetadataReaderFactoryCache());

	@Override
	public boolean canScan(String pattern) {
		if (super.canScan(pattern)) {
			return true;
		}

		String[] packageNames = StringUtils.splitToArray(pattern);
		return CollectionUtils.isAny(Arrays.asList(packageNames), (e) -> StringUtils.verifyPackageName(e));
	}

	@Override
	public ServiceLoader<Class<?>> scan(String pattern, @Nullable ClassLoader classLoader, TypeFilter filter) {
		if (super.canScan(pattern)) {
			return super.scan(pattern, classLoader, filter);
		}

		String[] packageNames = StringUtils.splitToArray(pattern);
		ServiceLoaders<Class<?>> editableClassesLoader = new ServiceLoaders<>();
		for (String name : packageNames) {
			if (!StringUtils.verifyPackageName(name)) {
				continue;
			}
			PackageClassesLoader packageClassesLoader = new PackageClassesLoader(
					getResourcePatternResolver(classLoader), name);
			packageClassesLoader.setTypeFilter(filter);
			packageClassesLoader.setMetadataReaderFactory(getMetadataReaderFactory(classLoader));
			editableClassesLoader.register(packageClassesLoader);
		}
		return editableClassesLoader;
	}

	public MetadataReaderFactory getMetadataReaderFactory(ClassLoader classLoader) {
		MetadataReaderFactoryCache metadataReaderFactory = metadataReaderFactoryMap.getClassLoaderProvider(classLoader);
		if (metadataReaderFactory == null) {
			metadataReaderFactory = new MetadataReaderFactoryCache(classLoader);
			metadataReaderFactoryMap.put(classLoader, metadataReaderFactory);
		}
		return metadataReaderFactory.getMetadataReaderFactory();
	}

	public void registerMetadataReaderFactory(ClassLoader classLoader, MetadataReaderFactory metadataReaderFactory) {
		metadataReaderFactoryMap.put(classLoader, new MetadataReaderFactoryCache(classLoader, metadataReaderFactory));
	}

	public ResourcePatternResolver getResourcePatternResolver(ClassLoader classLoader) {
		ResourcePatternResolver resourcePatternResolver = resourcePatternResolverMap
				.getClassLoaderProvider(classLoader);
		if (resourcePatternResolver == null) {
			resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
			resourcePatternResolverMap.put(classLoader, resourcePatternResolver);
		}
		return resourcePatternResolver;
	}

	public void registerResourcePatternResolver(ClassLoader classLoader,
			ResourcePatternResolver resourcePatternResolver) {
		resourcePatternResolverMap.put(classLoader, resourcePatternResolver);
	}

	@Data
	@RequiredArgsConstructor
	private static class MetadataReaderFactoryCache implements ClassLoaderProvider {
		private final ClassLoader classLoader;
		private final MetadataReaderFactory metadataReaderFactory;

		public MetadataReaderFactoryCache() {
			this(ClassUtils.getDefaultClassLoader());
		}

		public MetadataReaderFactoryCache(ClassLoader classLoader) {
			this(classLoader, new CachingMetadataReaderFactory(classLoader));
		}

		@Override
		public ClassLoader getClassLoader() {
			return classLoader;
		}
	}
}
