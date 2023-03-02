package io.basc.framework.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public interface RedisHashesCommands<K, V> {
	Long hdel(K key, K... fields);

	Boolean hexists(K key, K field);

	V hget(K key, K field);

	Map<K, V> hgetall(K key);

	Long hincrby(K key, K field, long increment);

	Double hincrbyfloat(K key, K field, double increment);

	Set<K> hkeys(K key);

	Long hlen(K key);

	List<V> hmget(K key, K... fields);

	String hmset(K key, Map<K, V> values);

	List<K> hrandfield(K key, Integer count);

	Map<K, V> hrandfieldWithValue(K key, Integer count);

	Long hset(K key, Map<K, V> values);

	Long hsetnx(K key, K field, V value);

	Long hstrlen(K key, K field);

	List<V> hvals(K key);
}
