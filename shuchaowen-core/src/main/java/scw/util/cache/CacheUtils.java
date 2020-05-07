package scw.util.cache;

import scw.lang.NotSupportedException;

public class CacheUtils {
	private CacheUtils() {
	};

	public static <K, V> CacheOperations<K, V> createLocalCache(
			LocalCacheType localCacheType) {
		switch (localCacheType) {
		case NONE:
			return new NoneCacheOperations<K, V>();
		case HASH_MAP:
			return new HashMapCacheOperations<K, V>();
		case CONCURRENT_HASH_MAP:
			return new ConcurrentMapCacheOperations<K, V>(false);
		case CONCURRENT_REFERENCE_HASH_MAP:
			return new ConcurrentMapCacheOperations<K, V>(true);
		default:
			throw new NotSupportedException("not support local cache type: "
					+ localCacheType.name());
		}
	}
}
