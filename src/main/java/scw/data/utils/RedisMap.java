package scw.data.utils;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Constants;
import scw.data.redis.Redis;
import scw.data.redis.RedisCommands;
import scw.data.redis.RedisUtils;
import scw.data.redis.serialize.RedisSerialize;

public class RedisMap<V> implements scw.data.utils.Map<String, V> {
	private static final String REMOVE_SCRIPT = "local old = redis.call('HGET', KEYS[1], KEYS[2]) redis.call('HDEL', KEYS[1], KEYS[2]) return old";
	private static final String PUT_SCRIPT = "local old = redis.call('HGET', KEYS[1], KEYS[2]) redis.call('HSET', KEYS[1], KEYS[2], ARGV[1]) return old";
	private static final String PUT_IFABSENT_SCRIPT = "local old = redis.call('HGET', KEYS[1], KEYS[2]) redis.call('HSETNX ', KEYS[1], KEYS[2], ARGV[1]) return old";

	private final RedisCommands<byte[], byte[]> commands;
	private final byte[] dataKey;
	private final Charset charset;
	private final RedisSerialize redisSerialize;

	public RedisMap(Redis redis, String key) {
		this(redis.getBinaryOperations(), key, Constants.DEFAULT_CHARSET,
				RedisUtils.DEFAULT_OBJECT_SERIALIZE);
	}

	public RedisMap(RedisCommands<byte[], byte[]> commands, String dataKey,
			Charset charset, RedisSerialize redisSerialize) {
		this.commands = commands;
		this.dataKey = dataKey.getBytes(charset);
		this.charset = charset;
		this.redisSerialize = redisSerialize;
	}

	public Charset getCharset() {
		return charset;
	}

	public RedisSerialize getRedisSerialize() {
		return redisSerialize;
	}

	public int size() {
		return (int) longSize();
	}

	public long longSize() {
		Long size = commands.hlen(dataKey);
		return size == null ? 0 : size;
	}

	public boolean isEmpty() {
		return longSize() == 0;
	}

	public V get(String key) {
		return null;
	}

	@SuppressWarnings("unchecked")
	public V remove(String key) {
		return (V) commands.eval(REMOVE_SCRIPT.getBytes(charset),
				Arrays.asList(dataKey, key.getBytes(charset)), null);
	}

	public boolean containsKey(String key) {
		return commands.hexists(dataKey, key.getBytes(charset));
	}

	@SuppressWarnings("unchecked")
	public V put(String key, V value) {
		return (V) commands.eval(PUT_SCRIPT.getBytes(charset),
				Arrays.asList(dataKey, key.getBytes(charset)),
				Arrays.asList(redisSerialize.serialize(key, value)));
	}

	@SuppressWarnings("unchecked")
	public V putIfAbsent(String key, V value) {
		return (V) commands.eval(PUT_IFABSENT_SCRIPT.getBytes(charset),
				Arrays.asList(dataKey, key.getBytes(charset)),
				Arrays.asList(redisSerialize.serialize(key, value)));
	}

	public void putAll(java.util.Map<? extends String, ? extends V> m) {
		if (m == null || m.isEmpty()) {
			return;
		}
		Map<byte[], byte[]> data = new LinkedHashMap<byte[], byte[]>(m.size(),
				1);
		for (Entry<? extends String, ? extends V> entry : m.entrySet()) {
			data.put(entry.getKey().getBytes(charset),
					redisSerialize.serialize(entry.getKey(), entry.getValue()));
		}
		commands.hmset(dataKey, data);
	}

	@SuppressWarnings("unchecked")
	public java.util.Map<String, V> asMap() {
		java.util.Map<byte[], byte[]> map = commands.hgetAll(dataKey);
		if (map == null || map.isEmpty()) {
			return null;
		}

		Map<String, V> data = new LinkedHashMap<String, V>(map.size(), 1);
		for (Entry<byte[], byte[]> entry : map.entrySet()) {
			String key = new String(entry.getKey(), charset);
			data.put(key, (V) redisSerialize.deserialize(key, entry.getValue()));
		}
		return data;
	}

}
