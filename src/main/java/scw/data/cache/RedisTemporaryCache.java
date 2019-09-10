package scw.data.cache;

import scw.data.redis.Redis;

public final class RedisTemporaryCache implements TemporaryCache {
	private Redis redis;

	public RedisTemporaryCache(Redis redis) {
		this.redis = redis;
	}

	public Object get(String key) {
		return redis.getObjectOperations().get(key);
	}

	public Object getAndTouch(String key, int exp) {
		return redis.getObjectOperations().getAndTouch(key, exp);
	}

	public void touch(String key, int exp) {
		redis.getObjectOperations().getAndTouch(key, exp);
	}

	public void delete(String key) {
		redis.getObjectOperations().del(key);
	}

	public void set(String key, int exp, Object value) {
		redis.getObjectOperations().setex(key, exp, value);
	}

}
