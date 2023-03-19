package io.basc.framework.util;

import java.util.Set;
import java.util.stream.Stream;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public class ResourceClassesLoader extends DefaultClassLoaderProvider implements ClassesLoader {
	private static Logger logger = LoggerFactory.getLogger(ResourceClassesLoader.class);
	private volatile Set<Class<?>> caching;

	private boolean disableCache;

	private volatile MetadataReaderFactory metadataReaderFactory;

	private final ResultSet<Resource> resources;

	private TypeFilter typeFilter;

	public ResourceClassesLoader(ResultSet<Resource> resources) {
		Assert.requiredArgument(resources != null, "resources");
		this.resources = resources;
	}

	public Set<Class<?>> getCaching() {
		if (caching == null) {
			synchronized (this) {
				if (caching == null) {
					caching = load().toSet();
				}
			}
		}
		return caching;
	}

	public MetadataReaderFactory getMetadataReaderFactory() {
		if (metadataReaderFactory == null) {
			synchronized (this) {
				if (metadataReaderFactory == null) {
					metadataReaderFactory = new CachingMetadataReaderFactory(getClassLoader());
				}
			}
		}
		return metadataReaderFactory;
	}

	public ResultSet<Resource> getResources() {
		return resources;
	}

	public TypeFilter getTypeFilter() {
		return typeFilter;
	}

	public boolean isDisableCache() {
		return disableCache;
	}

	@Override
	public Cursor<Class<?>> iterator() {
		if (isDisableCache()) {
			return load();
		}
		return Cursor.of(getCaching());
	}

	public Cursor<Class<?>> load() {
		Stream<Class<?>> stream = resources.stream().map((resource) -> {
			if (resource == null) {
				return null;
			}

			MetadataReaderFactory factory = getMetadataReaderFactory();
			try {
				MetadataReader reader = factory.getMetadataReader(resource);
				if (reader == null) {
					return null;
				}

				TypeFilter typeFilter = getTypeFilter();
				if (typeFilter != null && !typeFilter.match(reader, factory)) {
					return null;
				}

				return ClassUtils.getClass(reader.getClassMetadata().getClassName(), getClassLoader());
			} catch (Throwable e) {
				logger.error(e, "Failed to load class from resource {}", resource);
				return null;
			}
		});
		return Cursor.of(stream.filter((e) -> e != null));
	}

	@Override
	public void reload() {
		if (metadataReaderFactory instanceof CachingMetadataReaderFactory) {
			((CachingMetadataReaderFactory) metadataReaderFactory).clearCache();
		}

		if (isDisableCache()) {
			synchronized (this) {
				this.caching = null;
			}
		} else {
			if (caching != null) {
				synchronized (this) {
					if (caching != null) {
						this.caching = load().toSet();
					}
				}
			}
		}
	}

	public void setDisableCache(boolean disableCache) {
		this.disableCache = disableCache;
	}

	public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
		this.metadataReaderFactory = metadataReaderFactory;
	}

	public void setTypeFilter(TypeFilter typeFilter) {
		this.typeFilter = typeFilter;
	}
}
