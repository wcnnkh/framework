package io.basc.framework.core.scan;

import java.util.function.Supplier;

import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.io.load.ResourcePatternResolver;
import lombok.NonNull;

public class PackagePatternMetadataReaders extends LocationPatternMetadataReaders {
	private static final String ALL_LOCATION_PATTERN = "/**/*" + ClassUtils.CLASS_FILE_SUFFIX;

	public PackagePatternMetadataReaders(@NonNull ResourcePatternResolver resourcePatternResolver,
			@NonNull MetadataReaderFactory metadataReaderFactory, Supplier<String> packageNameSupplier,
			ResourceFilter resourceFilter) {
		super(resourcePatternResolver, metadataReaderFactory,
				Assert.requiredArgument(packageNameSupplier != null, "packageNameSupplier", () -> {
					String locationPattern = packageNameSupplier.get();
					if (StringUtils.isEmpty(locationPattern)) {
						return ALL_LOCATION_PATTERN;
					}

					locationPattern = ClassUtils.convertClassNameToResourcePath(locationPattern);
					if (locationPattern.endsWith("/")) {
						return locationPattern + "*" + ClassUtils.CLASS_FILE_SUFFIX;
					}

					if (locationPattern.lastIndexOf("*") != -1) {
						return locationPattern + ClassUtils.CLASS_FILE_SUFFIX;
					}

					return locationPattern + "/**/*" + ClassUtils.CLASS_FILE_SUFFIX;
				}), resourceFilter);
	}

}
