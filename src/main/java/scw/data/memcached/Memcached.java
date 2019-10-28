package scw.data.memcached;

import scw.beans.annotation.AutoImpl;
import scw.data.cache.CacheService;
import scw.data.cas.CASOperations;

@AutoImpl(className = { "scw.data.memcached.x.XMemcached" })
public interface Memcached extends CacheService {
	CASOperations getCASOperations();
}
