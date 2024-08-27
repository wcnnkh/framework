package io.basc.framework.io.scan;

import java.io.IOException;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.Resource;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import lombok.Data;
import lombok.NonNull;

@Data
public class MetadataReaders implements ServiceLoader<MetadataReader> {
	private static Logger logger = LoggerFactory.getLogger(MetadataReaders.class);
	@NonNull
	private final ServiceLoader<? extends Resource> resources;
	@NonNull
	private final MetadataReaderFactory metadataReaderFactory;
	private TypeFilter typeFilter;

	public void andTypeFilter(TypeFilter typeFilter) {
		if (this.typeFilter == null) {
			this.typeFilter = typeFilter;
		} else {
			this.typeFilter = this.typeFilter.and(typeFilter);
		}
	}

	public void orTypeFilter(TypeFilter typeFilter) {
		if (this.typeFilter == null) {
			this.typeFilter = typeFilter;
		} else {
			this.typeFilter = this.typeFilter.or(typeFilter);
		}
	}

	@Override
	public void reload() {
		resources.reload();
		if (metadataReaderFactory instanceof CachingMetadataReaderFactory) {
			((CachingMetadataReaderFactory) metadataReaderFactory).clearCache();
		}
	}

	@Override
	public Elements<MetadataReader> getServices() {
		return resources.getServices().map((resource) -> {
			MetadataReader metadataReader;
			try {
				metadataReader = metadataReaderFactory.getMetadataReader(resource);
			} catch (IOException e) {
				logger.error(e, "Get metadataReader error for {}", resource);
				return null;
			}
			if (metadataReader == null) {
				return null;
			}

			try {
				if (typeFilter == null || typeFilter.match(metadataReader, metadataReaderFactory)) {
					return metadataReader;
				}
			} catch (IOException e) {
				logger.error(e, " match error for {}", resource);
				return null;
			}
			return null;
		}).filter((e) -> e != null);
	}

}
