package scw.db.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import scw.core.Constants;
import scw.redis.Redis;

public final class RedisCache implements Cache {
	private final Redis redis;

	public RedisCache(Redis redis) {
		this.redis = redis;
	}

	public void add(String key, Object value, int exp) {
		redis.getBinaryOperations().set(key.getBytes(Constants.DEFAULT_CHARSET), CacheUtils.encode(value),
				Redis.NX.getBytes(Constants.DEFAULT_CHARSET), Redis.EX.getBytes(Constants.DEFAULT_CHARSET), exp);
	}

	public void set(String key, Object value, int exp) {
		redis.getBinaryOperations().set(key.getBytes(Constants.DEFAULT_CHARSET), CacheUtils.encode(value),
				Redis.XX.getBytes(Constants.DEFAULT_CHARSET), Redis.EX.getBytes(Constants.DEFAULT_CHARSET), exp);
	}

	public void delete(String key) {
		redis.getBinaryOperations().del(key.getBytes(Constants.DEFAULT_CHARSET));
	}

	public <T> T get(Class<T> type, String key) {
		byte[] data = redis.getBinaryOperations().get(key.getBytes(Constants.DEFAULT_CHARSET));
		if (data == null) {
			return null;
		}

		return CacheUtils.decode(type, data);
	}

	public <T> T getAndTouch(Class<T> type, String key, int exp) {
		byte[] data = redis.getBinaryOperations().getAndTouch(key.getBytes(Constants.DEFAULT_CHARSET), exp);
		if (data == null) {
			return null;
		}

		return CacheUtils.decode(type, data);
	}

	public <T> Map<String, T> get(Class<T> type, Collection<String> keys) {
		byte[][] arr = new byte[keys.size()][];
		Iterator<String> iterator = keys.iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			arr[i] = iterator.next().getBytes(Constants.DEFAULT_CHARSET);
		}

		List<byte[]> list = redis.getBinaryOperations().mget(arr);
		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<String, T> valueMap = new HashMap<String, T>(arr.length);
		iterator = keys.iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			String key = iterator.next();
			byte[] data = list.get(i);
			if (data == null) {
				continue;
			}

			valueMap.put(key, CacheUtils.decode(type, data));
		}
		return valueMap;
	}

	public Map<String, String> getMap(String key) {
		return redis.getStringOperations().hgetAll(key);
	}

	public void mapAdd(String key, String field, String value) {
		redis.getStringOperations().hsetnx(key, field, value);
	}

	public void mapRemove(String key, String field) {
		redis.getStringOperations().hdel(key, field);
	}

}
