package io.basc.framework.redis;

import java.util.List;
import java.util.Set;

import io.basc.framework.util.page.Pageable;

@SuppressWarnings("unchecked")
public interface RedisSetsCommands<K, V> {
	Long sadd(K key, V... members);

	Long scard(K key);

	Set<V> sdiff(K... keys);

	Long sdiffstore(K destinationKey, K... keys);

	Set<V> sinter(K... keys);

	Long sinterstore(K destinationKey, K... keys);

	Boolean sismember(K key, V member);

	Set<V> smembers(K key);

	List<Boolean> smismember(K key, V... members);

	Boolean sMove(K sourceKey, K destinationKey, V member);

	Set<V> spop(K key, int count);

	List<V> srandmember(K key, int count);

	Long srem(K key, V... members);

	Set<V> sunion(K... keys);

	Long sunionstore(K destinationKey, K... keys);

	Pageable<Long, K> sScan(long cursorId, K key, ScanOptions<K> options);
}
