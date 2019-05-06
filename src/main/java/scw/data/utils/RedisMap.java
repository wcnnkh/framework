package scw.data.utils;

import java.util.Arrays;
import java.util.Map;

import scw.data.redis.Redis;
import scw.data.redis.RedisOperations;

public class RedisMap<V> implements scw.data.utils.Map<String, V> {
	private static final String REMOVE_SCRIPT = "local old = redis.call('HGET', KEYS[1], KEYS[2]) redis.call('HDEL', KEYS[1], KEYS[2]) return old";
	private static final String PUT_SCRIPT = "local old = redis.call('HGET', KEYS[1], KEYS[2]) redis.call('HSET', KEYS[1], KEYS[2], ARGV[1]) return old";
	private static final String PUT_IFABSENT_SCRIPT = "local old = redis.call('HGET', KEYS[1], KEYS[2]) redis.call('HSETNX ', KEYS[1], KEYS[2], ARGV[1]) return old";

	private final RedisOperations<String, Object> operations;
	private final String dataKey;

	public RedisMap(Redis redis, String key) {
		this(redis.getObjectOperations(), key);
	}

	public RedisMap(RedisOperations<String, Object> operations, String dataKey) {
		this.operations = operations;
		this.dataKey = dataKey;
	}
	
	public int size() {
		return (int) longSize();
	}

	public long longSize() {
		Long size = operations.hlen(dataKey);
		return size == null ? 0 : size;
	}

	public boolean isEmpty() {
		return longSize() == 0;
	}

	public V get(String key) {
		return null;
	}

	public boolean containsKey(String key) {
		return operations.hexists(dataKey, key);
	}

	@SuppressWarnings("unchecked")
	public void putAll(java.util.Map<? extends String, ? extends V> m) {
		operations.hmset(dataKey, (Map<String, Object>) m);
	}

	@SuppressWarnings("unchecked")
	public java.util.Map<String, V> asMap() {
		return (Map<String, V>) operations.hgetAll(dataKey);
	}

	@SuppressWarnings("unchecked")
	public V remove(String key) {
		return (V) operations.eval(REMOVE_SCRIPT, Arrays.asList(dataKey, key), null);
	}

	@SuppressWarnings("unchecked")
	public V put(String key, V value) {
		return (V) operations.eval(PUT_SCRIPT, Arrays.asList(dataKey, key), Arrays.asList(key, value));
	}

	@SuppressWarnings("unchecked")
	public V putIfAbsent(String key, V value) {
		return (V) operations.eval(PUT_IFABSENT_SCRIPT, Arrays.asList(dataKey, key), Arrays.asList(key, value));
	}

}
