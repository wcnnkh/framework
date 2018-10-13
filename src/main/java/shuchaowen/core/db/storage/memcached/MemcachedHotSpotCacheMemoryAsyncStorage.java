package shuchaowen.core.db.storage.memcached;

import shuchaowen.core.cache.Memcached;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.async.MemoryAsyncStorage;

/**
 * 此方式只能在单机环境一使用，因为在集群下无法保证执行顺序
 * @author shuchaowen
 *
 */
public class MemcachedHotSpotCacheMemoryAsyncStorage extends MemcachedHotSpotCacheStorage{

	public MemcachedHotSpotCacheMemoryAsyncStorage(AbstractDB db, Memcached memcached) {
		super(db, memcached, new MemoryAsyncStorage(db));
	}
	
	public MemcachedHotSpotCacheMemoryAsyncStorage(AbstractDB db, String prefix, int exp, Memcached memcached){
		super(db, prefix, exp, memcached, new MemoryAsyncStorage(db));
	}
}
