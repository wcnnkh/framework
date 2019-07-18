package scw.db.cache;

import java.util.Collection;
import java.util.Map;

import scw.data.redis.Redis;

public final class RedisLazyCacheManager extends LazyCacheManager {
	private final Redis redis;

	public RedisLazyCacheManager(Redis redis, String keyPrefix) {
		super(keyPrefix);
		this.redis = redis;
	}

	@Override
	protected void set(String key, int exp, Object value) {
		redis.getObjectOperations().setex(key, exp, value);
	}

	@Override
	protected void del(String key) {
		redis.getObjectOperations().del(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T getAndTouch(Class<T> type, String key, int exp) {
		return (T) redis.getObjectOperations().getAndTouch(key, exp);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> Map<String, T> mget(Class<T> type, Collection<String> keys) {
		return (Map<String, T>) redis.getObjectOperations().mget(keys);
	}

	@Override
	protected void addKey(String key) {
		redis.getObjectOperations().set(key, "");
	}

	@Override
	protected boolean isExist(String key) {
		return redis.getObjectOperations().exists(key);
	}
}
