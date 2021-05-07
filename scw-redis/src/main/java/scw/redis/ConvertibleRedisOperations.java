package scw.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import scw.codec.Codec;
import scw.core.utils.CollectionUtils;
import scw.redis.enums.EXPX;
import scw.redis.enums.NXXX;
import scw.value.AnyValue;

public class ConvertibleRedisOperations<K, WK, V, WV> implements RedisOperations<K, V> {
	private final RedisOperations<WK, WV> redisOperations;
	private final Codec<K, WK> keyCodec;
	private final Codec<V, WV> valueCodec;
	
	public ConvertibleRedisOperations(RedisOperations<WK, WV> redisOperations, Codec<K, WK> keyCodec, Codec<V, WV> valueCodec) {
		this.redisOperations = redisOperations;
		this.keyCodec = keyCodec;
		this.valueCodec = valueCodec;
	}

	public Codec<K, WK> getKeyCodec() {
		return keyCodec;
	}

	public Codec<V, WV> getValueCodec() {
		return valueCodec;
	}

	public RedisOperations<WK, WV> getRedisOperations() {
		return redisOperations;
	}

	@SuppressWarnings("unchecked")
	protected Map<WK, WV> encodeMap(Map<K, V> map) {
		if (CollectionUtils.isEmpty(map)) {
			return Collections.EMPTY_MAP;
		}

		Map<WK, WV> dataMap = new HashMap<WK, WV>();
		for (Entry<K, V> entry : map.entrySet()) {
			dataMap.put(getKeyCodec().encode(entry.getKey()), getValueCodec().encode(entry.getValue()));
		}
		return dataMap;
	}

	protected Map<K, V> decodeMap(Map<WK, WV> map) {
		if (CollectionUtils.isEmpty(map)) {
			return new HashMap<K, V>(4);
		}

		Map<K, V> dataMap = new HashMap<K, V>(map.size());
		for (Entry<WK, WV> entry : map.entrySet()) {
			dataMap.put(getKeyCodec().decode(entry.getKey()), getValueCodec().decode(entry.getValue()));
		}
		return dataMap;
	}

	protected Set<V> decodeValue(Set<WV> values) {
		if (CollectionUtils.isEmpty(values)) {
			return null;
		}

		LinkedHashSet<V> set = new LinkedHashSet<V>(values.size());
		for (WV wv : values) {
			if (wv == null) {
				continue;
			}

			set.add(getValueCodec().decode(wv));
		}
		return set;
	}

	public V get(K key) {
		return getValueCodec().decode(getRedisOperations().get(getKeyCodec().encode(key)));
	}

	public void set(K key, V value) {
		getRedisOperations().set(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	public boolean setnx(K key, V value) {
		return getRedisOperations().setnx(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	public void setex(K key, int seconds, V value) {
		getRedisOperations().setex(getKeyCodec().encode(key), seconds, getValueCodec().encode(value));
	}

	public boolean exists(K key) {
		return getRedisOperations().exists(getKeyCodec().encode(key));
	}

	public Long expire(K key, int seconds) {
		return getRedisOperations().expire(getKeyCodec().encode(key), seconds);
	}

	public boolean del(K key) {
		return getRedisOperations().del(getKeyCodec().encode(key));
	}

	public Long hset(K key, K field, V value) {
		return getRedisOperations().hset(getKeyCodec().encode(key), getKeyCodec().encode(field), getValueCodec().encode(value));
	}

	public Long hsetnx(K key, K field, V value) {
		return getRedisOperations().hsetnx(getKeyCodec().encode(key), getKeyCodec().encode(field), getValueCodec().encode(value));
	}

	@SuppressWarnings("unchecked")
	public Long hdel(K key, K... fields) {
		WK[] wk = getKeyCodec().encode(fields);
		if (wk == null) {
			return 0L;
		}

		return getRedisOperations().hdel(getKeyCodec().encode(key), wk);
	}

	public Long hlen(K key) {
		return getRedisOperations().hlen(getKeyCodec().encode(key));
	}

	public Boolean hexists(K key, K field) {
		return getRedisOperations().hexists(getKeyCodec().encode(key), getKeyCodec().encode(field));
	}

	public Long ttl(K key) {
		return getRedisOperations().ttl(getKeyCodec().encode(key));
	}

	public Long incr(K key) {
		return getRedisOperations().incr(getKeyCodec().encode(key));
	}

	public Long decr(K key) {
		return getRedisOperations().decr(getKeyCodec().encode(key));
	}

	public Collection<V> hvals(K key) {
		return getValueCodec().decode(getRedisOperations().hvals(getKeyCodec().encode(key)));
	}

	public V hget(K key, K field) {
		return getValueCodec().decode(getRedisOperations().hget(getKeyCodec().encode(key), getKeyCodec().encode(field)));
	}

	@SuppressWarnings("unchecked")
	public Collection<V> hmget(K key, K... fields) {
		WK[] wk = getKeyCodec().encode(fields);
		if (wk == null) {
			return new ArrayList<V>(4);
		}

		return getValueCodec().decode(getRedisOperations().hmget(getKeyCodec().encode(key), wk));
	}

	@SuppressWarnings("unchecked")
	public Long lpush(K key, V... values) {
		WV[] wv = getValueCodec().encode(values);
		if (wv == null) {
			return 0L;
		}

		return getRedisOperations().lpush(getKeyCodec().encode(key), wv);
	}

	@SuppressWarnings("unchecked")
	public Long rpush(K key, V... values) {
		WV[] wv = getValueCodec().encode(values);
		if (wv == null) {
			return 0L;
		}

		return getRedisOperations().rpush(getKeyCodec().encode(key), wv);
	}

	public V rpop(K key) {
		return getValueCodec().decode(getRedisOperations().rpop(getKeyCodec().encode(key)));
	}

	public V lpop(K key) {
		return getValueCodec().decode(getRedisOperations().lpop(getKeyCodec().encode(key)));
	}

	public Set<V> smembers(K key) {
		return decodeValue(getRedisOperations().smembers(getKeyCodec().encode(key)));
	}

	@SuppressWarnings("unchecked")
	public Long srem(K key, V... members) {
		WV[] wv = getValueCodec().encode(members);
		if (wv == null) {
			return 0L;
		}

		return getRedisOperations().srem(getKeyCodec().encode(key), wv);
	}

	@SuppressWarnings("unchecked")
	public Long sadd(K key, V... members) {
		WV[] wv = getValueCodec().encode(members);
		if (wv == null) {
			return 0L;
		}

		return getRedisOperations().sadd(getKeyCodec().encode(key), wv);
	}

	public Long zadd(K key, long score, V member) {
		return getRedisOperations().zadd(getKeyCodec().encode(key), score, getValueCodec().encode(member));
	}

	public Boolean set(K key, V value, NXXX nxxx, EXPX expx, long time) {
		return getRedisOperations().set(getKeyCodec().encode(key), getValueCodec().encode(value), nxxx, expx, time);
	}

	public Boolean sIsMember(K key, V member) {
		return getRedisOperations().sIsMember(getKeyCodec().encode(key), getValueCodec().encode(member));
	}

	public V lindex(K key, int index) {
		return getValueCodec().decode(getRedisOperations().lindex(getKeyCodec().encode(key), index));
	}

	public Long llen(K key) {
		return getRedisOperations().llen(getKeyCodec().encode(key));
	}

	public AnyValue[] eval(K script, List<K> keys, List<V> args) {
		return getRedisOperations().eval(getKeyCodec().encode(script), getKeyCodec().encode(keys), getValueCodec().encode(args));
	}

	public Map<K, V> hgetAll(K key) {
		return decodeMap(getRedisOperations().hgetAll(getKeyCodec().encode(key)));
	}

	public List<V> brpop(int timeout, K key) {
		return getValueCodec().decode(getRedisOperations().brpop(timeout, getKeyCodec().encode(key)));
	}

	public List<V> blpop(int timeout, K key) {
		return getValueCodec().decode(getRedisOperations().blpop(timeout, getKeyCodec().encode(key)));
	}

	public Boolean hmset(K key, Map<K, V> hash) {
		return getRedisOperations().hmset(getKeyCodec().encode(key), encodeMap(hash));
	}

	public V getAndTouch(K key, int newExp) {
		return getValueCodec().decode(getRedisOperations().getAndTouch(getKeyCodec().encode(key), newExp));
	}

	public Map<K, V> get(Collection<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return new LinkedHashMap<K, V>(4);
		}

		Map<WK, K> keyMap = new HashMap<WK, K>();
		for (K key : keys) {
			if (key == null) {
				continue;
			}

			keyMap.put(getKeyCodec().encode(key), key);
		}

		Map<WK, WV> map = getRedisOperations().get(keyMap.keySet());
		if (CollectionUtils.isEmpty(map)) {
			return new LinkedHashMap<K, V>();
		}

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Entry<WK, WV> entry : map.entrySet()) {
			K key = keyMap.get(entry.getKey());
			if (key == null) {
				continue;
			}
			result.put(key, getValueCodec().decode(entry.getValue()));
		}
		return result;
	}

	public long decr(K key, long delta) {
		return getRedisOperations().decr(getKeyCodec().encode(key), delta);
	}

	public long incr(K key, long delta) {
		return getRedisOperations().incr(getKeyCodec().encode(key), delta);
	}
}
