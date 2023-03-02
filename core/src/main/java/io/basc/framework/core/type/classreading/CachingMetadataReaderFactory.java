package io.basc.framework.core.type.classreading;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.lang.Nullable;

/**
 * Caching implementation of the {@link MetadataReaderFactory} interface,
 * caching a {@link MetadataReader} instance per {@link Resource} handle (i.e.
 * per ".class" file).
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/classreading/CachingMetadataReaderFactory.java
 */
public class CachingMetadataReaderFactory extends SimpleMetadataReaderFactory {

	/** Default maximum number of entries for a local MetadataReader cache: 256. */
	public static final int DEFAULT_CACHE_LIMIT = 256;

	/** MetadataReader cache: either local or shared at the ResourceLoader level. */
	@Nullable
	private Map<Resource, MetadataReader> metadataReaderCache;

	/**
	 * Create a new CachingMetadataReaderFactory for the default class loader, using
	 * a local resource cache.
	 */
	public CachingMetadataReaderFactory() {
		super();
		setCacheLimit(DEFAULT_CACHE_LIMIT);
	}

	/**
	 * Create a new CachingMetadataReaderFactory for the given {@link ClassLoader},
	 * using a local resource cache.
	 * 
	 * @param classLoader the ClassLoader to use
	 */
	public CachingMetadataReaderFactory(@Nullable ClassLoader classLoader) {
		super(classLoader);
		setCacheLimit(DEFAULT_CACHE_LIMIT);
	}

	/**
	 * Create a new CachingMetadataReaderFactory for the given
	 * {@link ResourceLoader}, using a shared resource cache if supported or a local
	 * resource cache otherwise.
	 * 
	 * @param resourceLoader the ResourceLoader to use (also determines the
	 *                       ClassLoader to use)
	 */
	public CachingMetadataReaderFactory(@Nullable ResourceLoader resourceLoader) {
		super(resourceLoader);
		setCacheLimit(DEFAULT_CACHE_LIMIT);
	}

	public void setCacheLimit(int cacheLimit) {
		if (cacheLimit <= 0) {
			this.metadataReaderCache = null;
		} else if (this.metadataReaderCache instanceof LocalResourceCache) {
			((LocalResourceCache) this.metadataReaderCache).setCacheLimit(cacheLimit);
		} else {
			this.metadataReaderCache = new LocalResourceCache(cacheLimit);
		}
	}

	public int getCacheLimit() {
		if (this.metadataReaderCache instanceof LocalResourceCache) {
			return ((LocalResourceCache) this.metadataReaderCache).getCacheLimit();
		} else {
			return (this.metadataReaderCache != null ? Integer.MAX_VALUE : 0);
		}
	}

	@Override
	public MetadataReader getMetadataReader(Resource resource) throws IOException {
		if (this.metadataReaderCache instanceof ConcurrentMap) {
			// No synchronization necessary...
			MetadataReader metadataReader = this.metadataReaderCache.get(resource);
			if (metadataReader == null) {
				metadataReader = super.getMetadataReader(resource);
				this.metadataReaderCache.put(resource, metadataReader);
			}
			return metadataReader;
		} else if (this.metadataReaderCache != null) {
			// TODO 目前不可能到这里
			synchronized (this.metadataReaderCache) {
				MetadataReader metadataReader = this.metadataReaderCache.get(resource);
				if (metadataReader == null) {
					metadataReader = super.getMetadataReader(resource);
					this.metadataReaderCache.put(resource, metadataReader);
				}
				return metadataReader;
			}
		} else {
			return super.getMetadataReader(resource);
		}
	}

	public void clearCache() {
		if (this.metadataReaderCache instanceof LocalResourceCache) {
			synchronized (this.metadataReaderCache) {
				this.metadataReaderCache.clear();
			}
		} else if (this.metadataReaderCache != null) {
			// Shared resource cache -> reset to local cache.
			setCacheLimit(DEFAULT_CACHE_LIMIT);
		}
	}

	@SuppressWarnings("serial")
	private static class LocalResourceCache extends LinkedHashMap<Resource, MetadataReader> {

		private volatile int cacheLimit;

		public LocalResourceCache(int cacheLimit) {
			super(cacheLimit, 0.75f, true);
			this.cacheLimit = cacheLimit;
		}

		public void setCacheLimit(int cacheLimit) {
			this.cacheLimit = cacheLimit;
		}

		public int getCacheLimit() {
			return this.cacheLimit;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry<Resource, MetadataReader> eldest) {
			return size() > this.cacheLimit;
		}
	}

}
