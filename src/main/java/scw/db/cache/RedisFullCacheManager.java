package scw.db.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.data.redis.Redis;
import scw.data.redis.enums.EXPX;
import scw.data.redis.enums.NXXX;

public final class RedisFullCacheManager extends FullCacheManager {
	private final Redis redis;

	public RedisFullCacheManager(Redis redis) {
		this.redis = redis;
	}

	public void add(String key, Object value) {
		redis.getObjectOperations().set(key, value, NXXX.NX,
				EXPX.EX, 0);
	}

	public void set(String key, Object value) {
		redis.getObjectOperations().set(key, value, NXXX.XX,
				EXPX.EX, 0);
	}

	public void delete(String key) {
		redis.getObjectOperations().del(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> type, String key) {
		return (T) redis.getObjectOperations().get(key);
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
