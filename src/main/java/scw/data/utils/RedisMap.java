package scw.data.utils;

import java.nio.charset.Charset;
import java.util.Arrays;

import scw.data.redis.RedisCommands;

//TODO 暂未完成
public final class RedisMap<V> implements scw.data.utils.Map<String, V> {
	private static final String REMOVE_SCRIPT = "local old = redis.call('HGET', KEYS[1], KEYS[2]) redis.call('HDEL', KEYS[1], KEYS[2]) return old";
	private static final String PUT_SCRIPT = "local old = redis.call('HGET', KEYS[1], KEYS[2]) redis.call('HSET', KEYS[1], KEYS[2], ARGV[1]) return old";
	private static final String PUT_IFABSENT_SCRIPT = "local old = redis.call('HGET', KEYS[1], KEYS[2]) redis.call('HSETNX ', KEYS[1], KEYS[2], ARGV[1]) return old";

	private final RedisCommands<byte[], byte[]> commands;
	private final byte[] dataKey;
	private final Charset charset;

	public RedisMap(RedisCommands<byte[], byte[]> commands, String dataKey, Charset charset) {
		this.commands = commands;
		this.dataKey = dataKey.getBytes(charset);
		this.charset = charset;
	}

	public Charset getCharset() {
		return charset;
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
		return (V) commands.eval(REMOVE_SCRIPT.getBytes(charset), Arrays.asList(dataKey, key.getBytes(charset)), null);
	}

	public boolean containsKey(String key) {
		return commands.hexists(dataKey, key.getBytes(charset));
	}

	@SuppressWarnings("unchecked")
	public V put(String key, V value) {
		return (V) commands.eval(PUT_SCRIPT.getBytes(charset), Arrays.asList(dataKey, key.getBytes(charset)), null);
	}

	@SuppressWarnings("unchecked")
	public V putIfAbsent(String key, V value) {
		return (V) commands.eval(PUT_IFABSENT_SCRIPT.getBytes(charset), Arrays.asList(dataKey, key.getBytes(charset)),
				null);
	}

	public void putAll(java.util.Map<? extends String, ? extends V> m) {
		//
	}

	public java.util.Map<String, V> asMap() {
		java.util.Map<byte[], byte[]> map = commands.hgetAll(dataKey);
		return null;
	}

}
