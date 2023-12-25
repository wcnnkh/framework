package io.basc.framework.io.scan;

import io.basc.framework.core.type.AnnotationMetadata;
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
public class MetadataReaderScanner implements ClassScanner {
	private MetadataReaderFactory metadataReaderFactory;
	private ResourcePatternResolver resourcePatternResolver;
	private TypeFilter typeFilter;

	@Override
	public boolean canScan(String locationPattern) {
		return true;
	}

	public ServiceLoader<MetadataReader> getMetadataReaders(ResourcePatternResolver resourcePatternResolver,
			MetadataReaderFactory metadataReaderFactory, String locationPattern, @Nullable TypeFilter typeFilter) {
		LocationPatternMetadataReaders locationPatternMetadataReaders = new LocationPatternMetadataReaders(
				resourcePatternResolver, metadataReaderFactory, locationPattern);
		if (this.typeFilter != null) {
			locationPatternMetadataReaders.setTypeFilter(this.typeFilter);
		}

		if (typeFilter != null) {
			locationPatternMetadataReaders.andTypeFilter(typeFilter);
		}
		return locationPatternMetadataReaders;
	}

	public ServiceLoader<MetadataReader> getMetadataReaders(ResourcePatternResolver resourcePatternResolver,
			String locationPattern, @Nullable TypeFilter typeFilter) {
		return getMetadataReaders(resourcePatternResolver,
				metadataReaderFactory == null ? new CachingMetadataReaderFactory(resourcePatternResolver)
						: metadataReaderFactory,
				locationPattern, typeFilter);
	}

	public ServiceLoader<MetadataReader> getMetadataReaders(String locationPattern, @Nullable TypeFilter typeFilter) {
		return getMetadataReaders(
				resourcePatternResolver == null ? new PathMatchingResourcePatternResolver() : resourcePatternResolver,
				locationPattern, typeFilter);
	}

	@Override
	public Elements<AnnotationMetadata> scan(String locationPattern) {
		return getMetadataReaders(locationPattern, null).getServices().map((e) -> e.getAnnotationMetadata());
	}
}
