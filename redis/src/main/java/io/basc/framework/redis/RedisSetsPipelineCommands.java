package io.basc.framework.redis;

import java.util.List;
import java.util.Set;

import io.basc.framework.util.page.Cursor;

@SuppressWarnings("unchecked")
public interface RedisSetsPipelineCommands<K, V> {
	RedisResponse<Long> sadd(K key, V... members);

	RedisResponse<Long> scard(K key);

	RedisResponse<Set<V>> sdiff(K... keys);

	RedisResponse<Long> sdiffstore(K destinationKey, K... keys);

	RedisResponse<Set<V>> sinter(K... keys);

	RedisResponse<Long> sinterstore(K destinationKey, K... keys);

	RedisResponse<Boolean> sismember(K key, V member);

	RedisResponse<Set<V>> smembers(K key);

	RedisResponse<List<Boolean>> smismember(K key, V... members);

	RedisResponse<Long> sMove(K sourceKey, K destinationKey, V member);

	RedisResponse<Set<V>> spop(K key, int count);

	RedisResponse<List<V>> srandmember(K key, int count);

	RedisResponse<Long> srem(K key, V... members);

	RedisResponse<Set<V>> sunion(K... keys);

	RedisResponse<Long> sunionstore(K destinationKey, K... keys);

	RedisResponse<Cursor<Long, K>> sScan(long cursorId, K key, ScanOptions<K> options);
}
