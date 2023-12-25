package io.basc.framework.io.scan;

import java.util.function.Supplier;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import lombok.NonNull;

public class PackageMetadataReaders extends LocationMetadataReaders {

	public PackageMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			@NonNull MetadataReaderFactory metadataReaderFactory, String packageName) {
		super(resourcePatternResolver, metadataReaderFactory,
				Assert.requiredArgument(packageName != null, "packageName", () -> packageName));
	}

	public PackageMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver, String packageName) {
		this(resourcePatternResolver, new CachingMetadataReaderFactory(resourcePatternResolver), packageName);
	}

	public PackageMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			Supplier<String> packageNameSupplier) {
		this(resourcePatternResolver, new CachingMetadataReaderFactory(resourcePatternResolver), packageNameSupplier);
	}

	public PackageMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			@NonNull MetadataReaderFactory metadataReaderFactory, Supplier<String> packageNameSupplier) {
		super(resourcePatternResolver, metadataReaderFactory, Assert.requiredArgument(packageNameSupplier != null,
				"packageNameSupplier", () -> ClassUtils.convertClassNameToResourcePath(packageNameSupplier.get())));
	}

}
