package io.basc.framework.io.scan;

import java.util.function.Supplier;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.util.Assert;
import lombok.NonNull;

public class LocationMetadataReaders extends LocationPatternMetadataReaders {

	public LocationMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			@NonNull MetadataReaderFactory metadataReaderFactory, String location) {
		super(resourcePatternResolver, metadataReaderFactory,
				Assert.requiredArgument(location != null, "location", () -> location));
	}

	public LocationMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver, String location) {
		this(resourcePatternResolver, new CachingMetadataReaderFactory(resourcePatternResolver), location);
	}

	public LocationMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			Supplier<String> locationSupplier) {
		this(resourcePatternResolver, new CachingMetadataReaderFactory(resourcePatternResolver), locationSupplier);
	}

	public LocationMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			@NonNull MetadataReaderFactory metadataReaderFactory, Supplier<String> locationSupplier) {
		super(resourcePatternResolver, metadataReaderFactory, Assert.requiredArgument(locationSupplier != null,
				"locationSupplier", () -> ClassScanner.cleanLocation(locationSupplier.get())));
	}

}
