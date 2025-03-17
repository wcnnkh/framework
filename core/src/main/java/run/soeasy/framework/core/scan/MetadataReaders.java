package run.soeasy.framework.core.scan;

import java.io.IOException;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.type.classreading.CachingMetadataReaderFactory;
import run.soeasy.framework.core.type.classreading.MetadataReader;
import run.soeasy.framework.core.type.classreading.MetadataReaderFactory;
import run.soeasy.framework.core.type.filter.TypeFilter;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.collections.ServiceLoader;
import run.soeasy.framework.util.collections.ServiceLoader.ReloadableElementsWrapper;
import run.soeasy.framework.util.io.Resource;
import run.soeasy.framework.util.logging.LogManager;
import run.soeasy.framework.util.logging.Logger;

@Data
public class MetadataReaders implements ReloadableElementsWrapper<MetadataReader, Elements<MetadataReader>> {
	private static Logger logger = LogManager.getLogger(MetadataReaders.class);
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
	public Elements<MetadataReader> getSource() {
		return resources.map((resource) -> {
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
