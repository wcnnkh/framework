package scw.sql.orm.cache;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.redis.Redis;

public class RedisCache implements Cache {
	private final static Charset CHARSET = Charset.forName("UTF-8");
	private final Redis redis;
	private final int exp;

	public RedisCache(Redis redis, int exp) {
		this.redis = redis;
		this.exp = exp;
	}

	public <T> T get(Class<T> type, String key) {
		byte[] data = redis.getAndTouch(key.getBytes(CHARSET), exp);
		return CacheUtils.decode(type, data);
	}

	public void delete(String key) {
		redis.delete(key.getBytes(CHARSET));
	}

	public void add(String key, Object bean) {
		redis.set(key.getBytes(CHARSET), CacheUtils.encode(bean), Redis.NX.getBytes(CHARSET),
				Redis.EX.getBytes(CHARSET), exp);
	}

	public void set(String key, Object bean) {
		redis.set(key.getBytes(CHARSET), CacheUtils.encode(bean), Redis.XX.getBytes(CHARSET),
				Redis.EX.getBytes(CHARSET), exp);
	}

	public <T> Map<String, T> getMap(Class<T> type, Collection<String> keys) {
		if (keys.isEmpty()) {
			return null;
		}

		byte[][] bKeys = new byte[keys.size()][];
		Iterator<String> iterator = keys.iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			bKeys[i] = iterator.next().getBytes(CHARSET);
		}

		Map<byte[], byte[]> map = redis.get(bKeys);
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, T> valueMap = new HashMap<String, T>(map.size(), 1);
		for (Entry<byte[], byte[]> entry : map.entrySet()) {
			String key = new String(entry.getKey(), CHARSET);
			T v = CacheUtils.decode(type, entry.getValue());
			valueMap.put(key, v);
		}
		return valueMap;
	}

}
