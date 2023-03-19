package io.basc.framework.io;

import java.io.IOException;
import java.util.function.Supplier;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.ResourceClassesLoader;
import io.basc.framework.util.StaticSupplier;

public class LocationPatternClassesLoader extends ResourceClassesLoader {
	private static Logger logger = LoggerFactory.getLogger(LocationPatternClassesLoader.class);

	public LocationPatternClassesLoader(ResourcePatternResolver resourcePatternResolver, String locationPattern) {
		this(resourcePatternResolver, new StaticSupplier<>(locationPattern));
	}

	public LocationPatternClassesLoader(ResourcePatternResolver resourcePatternResolver,
			Supplier<String> locationPatternSupplier) {
		super(() -> {
			String location = locationPatternSupplier.get();
			if (location == null) {
				return Cursor.empty();
			}

			Resource[] resources = null;
			try {
				resources = resourcePatternResolver.getResources(location);
			} catch (IOException e) {
				logger.error(e, "Failed to obtain {} to obtain resources", location);
			}

			if (resources == null || resources.length == 0) {
				return Cursor.empty();
			}

			return Cursor.of(resources);
		});
		setMetadataReaderFactory(new CachingMetadataReaderFactory(resourcePatternResolver));
	}
}
