package io.basc.framework.io.scan;

import java.util.function.Supplier;

import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.io.ResourcePatternResolver;
import lombok.NonNull;

public class LocationPatternMetadataReaders extends MetadataReaders {
	@NonNull
	private final ResourcePatternResolver resourcePatternResolver;
	@NonNull
	private final Supplier<String> locationPatternSupplier;

	public LocationPatternMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			@NonNull MetadataReaderFactory metadataReaderFactory, Supplier<String> locationPatternSupplier,
			ResourceFilter resourceFilter) {
		super(new LocationPatternResources(resourcePatternResolver, locationPatternSupplier, resourceFilter),
				metadataReaderFactory);
		this.resourcePatternResolver = resourcePatternResolver;
		this.locationPatternSupplier = locationPatternSupplier;
	}
}
