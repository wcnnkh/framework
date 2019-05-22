package scw.db.cache;

import java.util.Collection;
import java.util.Map;

import scw.data.redis.Redis;

public class RedisLazyCacheManager extends LazyDataManager {
	private final Redis redis;

	public RedisLazyCacheManager(Redis redis, int exp, boolean key) {
		super(exp, key);
		this.redis = redis;
	}

	@Override
	protected void set(String key, Object value) {
		redis.getObjectOperations().setex(key, getExp(), value);
	}

	@Override
	protected void del(String key) {
		redis.getObjectOperations().del(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T get(Class<T> type, String key) {
		return (T) redis.getObjectOperations().get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> Map<String, T> mget(Class<T> type, Collection<String> keys) {
		return (Map<String, T>) redis.getObjectOperations().mget(keys);
	}

	@Override
	protected void addKey(String key) {
		redis.getStringOperations().set(key, "");
	}

	@Override
	protected boolean isExist(String key) {
		return redis.getStringOperations().exists(key);
	}
}
