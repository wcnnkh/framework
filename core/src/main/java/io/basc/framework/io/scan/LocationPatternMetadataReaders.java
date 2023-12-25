package io.basc.framework.io.scan;

import java.util.function.Supplier;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.util.Assert;
import lombok.NonNull;

public class LocationPatternMetadataReaders extends MetadataReaders {
	@NonNull
	private final ResourcePatternResolver resourcePatternResolver;
	@NonNull
	private final Supplier<String> locationPatternSupplier;

	public LocationPatternMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			String locationPattern) {
		this(resourcePatternResolver, new CachingMetadataReaderFactory(resourcePatternResolver), locationPattern);
	}

	public LocationPatternMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			@NonNull MetadataReaderFactory metadataReaderFactory, String locationPattern) {
		this(resourcePatternResolver, metadataReaderFactory,
				Assert.requiredArgument(locationPattern != null, "locationPattern", () -> locationPattern));
	}

	public LocationPatternMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			Supplier<String> locationPatternSupplier) {
		this(resourcePatternResolver, new CachingMetadataReaderFactory(resourcePatternResolver),
				locationPatternSupplier);
	}

	public LocationPatternMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			@NonNull MetadataReaderFactory metadataReaderFactory, Supplier<String> locationPatternSupplier) {
		super(new LocationPatternResources(resourcePatternResolver, locationPatternSupplier), metadataReaderFactory);
		this.resourcePatternResolver = resourcePatternResolver;
		this.locationPatternSupplier = locationPatternSupplier;
	}
}
