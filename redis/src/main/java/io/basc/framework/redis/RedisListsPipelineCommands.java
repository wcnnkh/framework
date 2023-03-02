package io.basc.framework.redis;

import java.util.List;

@SuppressWarnings("unchecked")
public interface RedisListsPipelineCommands<K, V> {
	RedisResponse<V> blmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to, long timout);

	default RedisResponse<List<V>> blpop(K... keys) {
		return blpop((double) 0, keys);
	}

	RedisResponse<List<V>> blpop(double timeout, K... keys);

	default RedisResponse<List<V>> brpop(K... keys) {
		return brpop((double) 0, keys);
	}

	RedisResponse<List<V>> brpop(double timeout, K... keys);

	RedisResponse<V> brpoplpush(K sourceKey, K destinationKey, double timout);

	RedisResponse<V> lindex(K key, long index);

	RedisResponse<Long> linsert(K key, InsertPosition position, V pivot, V value);

	RedisResponse<Long> llen(K key);

	RedisResponse<V> lmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to);

	RedisResponse<List<V>> lpop(K key, int count);

	RedisResponse<Long> lpush(K key, V... elements);

	RedisResponse<Long> lpushx(K key, V... elements);

	RedisResponse<List<V>> lrange(K key, long start, long stop);

	RedisResponse<Long> lrem(K key, int count, V element);

	RedisResponse<String> lset(K key, long index, V element);

	RedisResponse<String> ltrim(K key, long start, long stop);

	RedisResponse<List<V>> rpop(K key, int count);

	RedisResponse<V> rpoplpush(K sourceKey, K destinationKey);

	RedisResponse<Long> rpush(K key, V... elements);

	RedisResponse<Long> rpushx(K key, V... elements);
}
