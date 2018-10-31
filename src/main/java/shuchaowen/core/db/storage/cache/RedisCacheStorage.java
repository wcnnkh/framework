package shuchaowen.core.db.storage.cache;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.storage.async.RedisAsyncStorage;
import shuchaowen.redis.Redis;

public class RedisCacheStorage extends CacheStorage{

	public RedisCacheStorage(AbstractDB db, Redis redis) {
		super(new RedisCache(redis), new RedisAsyncStorage(db, redis, db.getClass().getName()));	
	}
	
	public RedisAsyncStorage getRedisAsyncStorage() {
		return (RedisAsyncStorage) getAsyncStroage();
	}

	public Redis getRedis() {
		return getRedisAsyncStorage().getRedis();
	}
}
