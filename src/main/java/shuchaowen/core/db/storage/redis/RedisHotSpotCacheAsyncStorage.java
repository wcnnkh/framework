package shuchaowen.core.db.storage.redis;

import shuchaowen.core.cache.Redis;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.async.RedisAsyncStorage;

public class RedisHotSpotCacheAsyncStorage extends RedisHotSpotCacheStorage{
	
	public RedisHotSpotCacheAsyncStorage(AbstractDB db, Redis redis, String queueKey) {
		super(db, redis, new RedisAsyncStorage(db, redis, queueKey));
	}
	
	public RedisHotSpotCacheAsyncStorage(RedisAsyncStorage asyncStorage) {
		super(asyncStorage.getDb(), asyncStorage.getRedis(), asyncStorage);
	}
	
	public RedisAsyncStorage getRedisAsyncStorage() {
		return (RedisAsyncStorage) getExecuteStorage();
	}
}
