package scw.data.redis.prefix;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.data.redis.RedisOperations;
import scw.data.redis.enums.EXPX;
import scw.data.redis.enums.NXXX;

public abstract class AbstractPrefixRedisOperationsWrapper<V> implements RedisOperations<String, V> {

	public abstract String getKeyPrefix();

	public abstract RedisOperations<String, V> getStringOperations();

	private String formatKey(String key) {
		String prefix = getKeyPrefix();
		return prefix == null ? key : (prefix + key);
	}

	public V get(String key) {
		return getStringOperations().get(formatKey(key));
	}

	public List<V> mget(String... keys) {
		String prefix = getKeyPrefix();
		if (StringUtils.isEmpty(prefix)) {
			return getStringOperations().mget(keys);
		} else {
			String[] formatKeys = new String[keys.length];
			for (int i = 0; i < keys.length; i++) {
				formatKeys[i] = formatKey(keys[i]);
			}

			return getStringOperations().mget(formatKeys);
		}
	}

	public void set(String key, V value) {
		getStringOperations().set(formatKey(key), value);
	}

	public boolean setnx(String key, V value) {
		return getStringOperations().setnx(formatKey(key), value);
	}

	public void setex(String key, int seconds, V value) {
		getStringOperations().setex(formatKey(key), seconds, value);
	}

	public boolean exists(String key) {
		return getStringOperations().exists(formatKey(key));
	}

	public Long expire(String key, int seconds) {
		return getStringOperations().expire(formatKey(key), seconds);
	}

	public boolean del(String key) {
		return getStringOperations().del(formatKey(key));
	}

	public Long hset(String key, String field, V value) {
		return getStringOperations().hset(formatKey(key), field, value);
	}

	public Long hsetnx(String key, String field, V value) {
		return getStringOperations().hsetnx(formatKey(key), field, value);
	}

	public Long hdel(String key, String... fields) {
		return getStringOperations().hdel(key, fields);
	}

	public Long hlen(String key) {
		return getStringOperations().hlen(key);
	}

	public Boolean hexists(String key, String field) {
		return getStringOperations().hexists(formatKey(key), field);
	}

	public Long ttl(String key) {
		return getStringOperations().ttl(formatKey(key));
	}

	public Long incr(String key) {
		return getStringOperations().incr(formatKey(key));
	}

	public Long decr(String key) {
		return getStringOperations().decr(formatKey(key));
	}

	public Collection<V> hvals(String key) {
		return getStringOperations().hvals(formatKey(key));
	}

	public V hget(String key, String field) {
		return getStringOperations().hget(formatKey(key), field);
	}

	public Collection<V> hmget(String key, String... fields) {
		return getStringOperations().hmget(formatKey(key), fields);
	}

	public Long lpush(String key, V... values) {
		return getStringOperations().lpush(formatKey(key), values);
	}

	public Long rpush(String key, V... values) {
		return getStringOperations().rpush(formatKey(key), values);
	}

	public V rpop(String key) {
		return getStringOperations().rpop(formatKey(key));
	}

	public V lpop(String key) {
		return getStringOperations().lpop(formatKey(key));
	}

	public Set<V> smembers(String key) {
		return getStringOperations().smembers(formatKey(key));
	}

	public Long srem(String key, V... members) {
		return getStringOperations().srem(formatKey(key), members);
	}

	public Long sadd(String key, V... members) {
		return getStringOperations().sadd(formatKey(key), members);
	}

	public Long zadd(String key, long score, V member) {
		return getStringOperations().zadd(formatKey(key), score, member);
	}

	public Boolean set(String key, V value, NXXX nxxx, EXPX expx, long time) {
		return getStringOperations().set(formatKey(key), value, nxxx, expx, time);
	}

	public Boolean sIsMember(String key, V member) {
		return getStringOperations().sIsMember(formatKey(key), member);
	}

	public V lindex(String key, int index) {
		return getStringOperations().lindex(formatKey(key), index);
	}

	public Long llen(String key) {
		return getStringOperations().llen(formatKey(key));
	}

	public Object eval(String script, List<String> keys, List<V> args) {
		return getStringOperations().eval(script, keys, args);
	}

	public Map<String, V> hgetAll(String key) {
		return getStringOperations().hgetAll(formatKey(key));
	}

	public List<V> brpop(int timeout, String key) {
		return getStringOperations().brpop(timeout, formatKey(key));
	}

	public List<V> blpop(int timeout, String key) {
		return getStringOperations().blpop(timeout, formatKey(key));
	}

	public Boolean hmset(String key, Map<String, V> hash) {
		return getStringOperations().hmset(formatKey(key), hash);
	}

	public long incr(String key, long incr, long initValue) {
		return getStringOperations().incr(formatKey(key), incr, initValue);
	}

	public long decr(String key, long decr, long initValue) {
		return getStringOperations().decr(formatKey(key), decr, initValue);
	}

	public V getAndTouch(String key, int newExp) {
		return getStringOperations().getAndTouch(formatKey(key), newExp);
	}

	public Map<String, V> mget(Collection<String> keys) {
		String prefix = getKeyPrefix();
		if (StringUtils.isEmpty(prefix)) {
			return getStringOperations().mget(keys);
		} else {
			if (CollectionUtils.isEmpty(keys)) {
				return new LinkedHashMap<String, V>();
			}

			Map<String, String> keyMap = new HashMap<String, String>(keys.size(), 1);
			for (String key : keys) {
				if (key == null) {
					continue;
				}

				keyMap.put(formatKey(key), key);
			}

			Map<String, V> map = getStringOperations().mget(keyMap.keySet());
			if (CollectionUtils.isEmpty(map)) {
				return new LinkedHashMap<String, V>(2);
			}

			Map<String, V> result = new LinkedHashMap<String, V>();
			for (Entry<String, V> entry : map.entrySet()) {
				String newKey = keyMap.get(entry.getKey());
				if (newKey == null) {
					continue;
				}

				result.put(newKey, entry.getValue());
			}
			return result;
		}
	}

	public long decr(String key, long delta) {
		return getStringOperations().decr(key, delta);
	}

	public long incr(String key, long delta) {
		return getStringOperations().decr(key, delta);
	}
}
