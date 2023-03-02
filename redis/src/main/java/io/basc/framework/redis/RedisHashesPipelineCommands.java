package io.basc.framework.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * https://redis.io/commands#hash
 * 
 * @author wcnnkh
 *
 */
@SuppressWarnings("unchecked")
public interface RedisHashesPipelineCommands<K, V> {
	RedisResponse<Long> hdel(K key, K... fields);

	RedisResponse<Boolean> hexists(K key, K field);

	RedisResponse<V> hget(K key, K field);

	RedisResponse<Map<K, V>> hgetall(K key);

	RedisResponse<Long> hincrby(K key, K field, long increment);

	RedisResponse<Double> hincrbyfloat(K key, K field, double increment);

	RedisResponse<Set<K>> hkeys(K key);

	RedisResponse<Long> hlen(K key);

	RedisResponse<List<V>> hmget(K key, K... fields);

	RedisResponse<String> hmset(K key, Map<K, V> values);

	RedisResponse<List<K>> hrandfield(K key, Integer count);

	RedisResponse<Map<K, V>> hrandfieldWithValue(K key, Integer count);

	RedisResponse<Long> hset(K key, Map<K, V> values);

	RedisResponse<Boolean> hsetnx(K key, K field, V value);

	RedisResponse<Long> hstrlen(K key, K field);

	RedisResponse<List<V>> hvals(K key);
}
