package run.soeasy.framework.core.scan;

import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.type.classreading.MetadataReaderFactory;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.io.load.ResourcePatternResolver;

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
