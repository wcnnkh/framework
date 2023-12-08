package io.basc.framework.io.loader;

import java.io.IOException;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;
import lombok.Data;
import lombok.NonNull;

@Data
public class MetadataReaderLoader implements ServiceLoader<MetadataReader> {
	private static Logger logger = LoggerFactory.getLogger(MetadataReaderLoader.class);
	@NonNull
	private final MetadataReaderFactory metadataReaderFactory;
	@NonNull
	private final Elements<? extends Resource> resources;
	private final TypeFilter typeFilter;

	@Override
	public Elements<MetadataReader> getServices() {
		Elements<MetadataReader> elements = resources.map((resource) -> {
			if (resource == null) {
				return null;
			}

			try {
				MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
				if (reader == null) {
					return null;
				}

				if (typeFilter != null && !typeFilter.match(reader, metadataReaderFactory)) {
					return null;
				}

				return reader;
			} catch (IOException e) {
				logger.error(e, "Failed to load class from resource {}", resource);
				return null;
			}
		});
		return elements.filter((e) -> e != null);
	}

	@Override
	public void reload() {
		if (metadataReaderFactory instanceof CachingMetadataReaderFactory) {
			((CachingMetadataReaderFactory) metadataReaderFactory).clearCache();
		}
	}
}
