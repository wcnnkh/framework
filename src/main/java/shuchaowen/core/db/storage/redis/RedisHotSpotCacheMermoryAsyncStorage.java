package shuchaowen.core.db.storage.redis;

import shuchaowen.core.cache.Redis;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.async.MemoryAsyncStorage;

/**
 * 此方式只能在单机环境一使用，因为在集群下无法保证执行顺序
 * @author shuchaowen
 *
 */
public class RedisHotSpotCacheMermoryAsyncStorage extends RedisHotSpotCacheStorage{

	public RedisHotSpotCacheMermoryAsyncStorage(AbstractDB db, Redis redis){
		super(db, redis, new MemoryAsyncStorage(db));
	}
	
	public RedisHotSpotCacheMermoryAsyncStorage(AbstractDB db,
			int exp, Redis redis) {
		super(db, exp, redis, new MemoryAsyncStorage(db));
	}
	
	public MemoryAsyncStorage getMemoryAsyncStorage(){
		return (MemoryAsyncStorage) getExecuteStorage();
	}
}
