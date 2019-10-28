package scw.data.cache;

import scw.data.memcached.Memcached;

public final class MemcachedCacheService extends CacheServiceWrapper implements CacheService {

	public MemcachedCacheService(Memcached memcached) {
		super(memcached);
	}
}
