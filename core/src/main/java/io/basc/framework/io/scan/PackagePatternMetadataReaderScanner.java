package io.basc.framework.io.scan;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackagePatternMetadataReaderScanner implements TypeScanner {
	private MetadataReaderFactory metadataReaderFactory;
	private ResourcePatternResolver resourcePatternResolver;
	private TypeFilter typeFilter;

	@Override
	public boolean canScan(String locationPattern) {
		return true;
	}

	@Override
	public Elements<MetadataReader> scan(String locationPattern, ResourceFilter resourceFilter, TypeFilter typeFilter) {
		return getMetadataReaders(locationPattern, resourceFilter, typeFilter).getServices();
	}

	public ServiceLoader<MetadataReader> getMetadataReaders(ResourcePatternResolver resourcePatternResolver,
			MetadataReaderFactory metadataReaderFactory, String locationPattern,
			@Nullable ResourceFilter resourceFilter, @Nullable TypeFilter typeFilter) {
		PackagePatternMetadataReaders locationPatternMetadataReaders = new PackagePatternMetadataReaders(
				resourcePatternResolver, metadataReaderFactory, () -> locationPattern, resourceFilter);
		if (this.typeFilter != null) {
			locationPatternMetadataReaders.setTypeFilter(this.typeFilter);
		}

		if (typeFilter != null) {
			locationPatternMetadataReaders.andTypeFilter(typeFilter);
		}
		return locationPatternMetadataReaders;
	}

	public ServiceLoader<MetadataReader> getMetadataReaders(ResourcePatternResolver resourcePatternResolver,
			String locationPattern, @Nullable ResourceFilter resourceFilter, @Nullable TypeFilter typeFilter) {
		return getMetadataReaders(resourcePatternResolver,
				metadataReaderFactory == null ? new CachingMetadataReaderFactory(resourcePatternResolver)
						: metadataReaderFactory,
				locationPattern, resourceFilter, typeFilter);
	}

	public ServiceLoader<MetadataReader> getMetadataReaders(String locationPattern,
			@Nullable ResourceFilter resourceFilter, @Nullable TypeFilter typeFilter) {
		return getMetadataReaders(
				resourcePatternResolver == null ? new PathMatchingResourcePatternResolver() : resourcePatternResolver,
				locationPattern, resourceFilter, typeFilter);
	}

}
