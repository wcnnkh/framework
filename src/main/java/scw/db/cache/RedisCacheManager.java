package scw.db.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.data.redis.Redis;
import scw.data.redis.RedisUtils;
import scw.sql.orm.TableFieldListen;

public final class RedisCacheManager implements CacheManager {
	private final Redis redis;

	public RedisCacheManager(Redis redis) {
		this.redis = redis;
	}

	public void add(String key, Object value, int exp) {
		redis.getObjectOperations().set(key, value, RedisUtils.NX, RedisUtils.EX, exp);
	}

	public void set(String key, Object value, int exp) {
		redis.getObjectOperations().set(key, value, RedisUtils.XX, RedisUtils.EX, exp);
	}

	public void delete(String key) {
		redis.getObjectOperations().del(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, String key) {
		T t = (T) redis.getObjectOperations().get(key);
		if (t != null && t instanceof TableFieldListen) {
			((TableFieldListen) t).start_field_listen();
		}
		return t;
	}

	public <T> T getAndTouch(Class<T> type, String key, int exp) {
		@SuppressWarnings("unchecked")
		T t = (T) redis.getObjectOperations().getAndTouch(key, exp);
		if (t != null && t instanceof TableFieldListen) {
			((TableFieldListen) t).start_field_listen();
		}
		return t;
	}

	public <T> Map<String, T> get(Class<T> type, Collection<String> keys) {
		if (type == null || CollectionUtils.isEmpty(keys)) {
			return null;
		}

		String[] keyArr = keys.toArray(new String[keys.size()]);
		@SuppressWarnings("unchecked")
		List<T> list = (List<T>) redis.getObjectOperations().mget(keyArr);
		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<String, T> valueMap = new HashMap<String, T>(keyArr.length);
		for (int i = 0; i < keyArr.length; i++) {
			T v = list.get(i);
			if (v == null) {
				continue;
			}

			valueMap.put(keyArr[i], v);
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
