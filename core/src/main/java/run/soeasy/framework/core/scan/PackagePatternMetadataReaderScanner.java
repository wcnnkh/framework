package run.soeasy.framework.core.scan;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.type.classreading.CachingMetadataReaderFactory;
import run.soeasy.framework.core.type.classreading.MetadataReader;
import run.soeasy.framework.core.type.classreading.MetadataReaderFactory;
import run.soeasy.framework.core.type.filter.TypeFilter;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.collections.ServiceLoader;
import run.soeasy.framework.util.io.load.PathMatchingResourcePatternResolver;
import run.soeasy.framework.util.io.load.ResourcePatternResolver;

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
		return getMetadataReaders(locationPattern, resourceFilter, typeFilter);
	}

	public ServiceLoader<MetadataReader> getMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			@NonNull MetadataReaderFactory metadataReaderFactory, @NonNull String locationPattern,
			ResourceFilter resourceFilter, TypeFilter typeFilter) {
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

	public ServiceLoader<MetadataReader> getMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			@NonNull String locationPattern, ResourceFilter resourceFilter, TypeFilter typeFilter) {
		return getMetadataReaders(resourcePatternResolver,
				metadataReaderFactory == null ? new CachingMetadataReaderFactory(resourcePatternResolver)
						: metadataReaderFactory,
				locationPattern, resourceFilter, typeFilter);
	}

	public ServiceLoader<MetadataReader> getMetadataReaders(@NonNull String locationPattern,
			ResourceFilter resourceFilter, TypeFilter typeFilter) {
		return getMetadataReaders(
				resourcePatternResolver == null ? new PathMatchingResourcePatternResolver() : resourcePatternResolver,
				locationPattern, resourceFilter, typeFilter);
	}

}
