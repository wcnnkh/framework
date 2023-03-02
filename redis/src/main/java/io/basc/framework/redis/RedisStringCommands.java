package io.basc.framework.redis;

import java.util.List;
import java.util.Map;

import io.basc.framework.lang.Nullable;

@SuppressWarnings("unchecked")
public interface RedisStringCommands<K, V> {
	Long append(K key, V value);

	Long bitcount(K key, long start, long end);

	Long bitop(BitOP op, K destkey, K... srcKeys);

	Long bitpos(K key, boolean bit, Long start, Long end);

	Long decr(K key);

	Long decrBy(K key, long decrement);

	V get(K key);

	Boolean getbit(K key, Long offset);

	V getdel(K key);

	V getEx(K key, ExpireOption option, Long time);

	V getrange(K key, long startOffset, long endOffset);

	V getset(K key, V value);

	Long incr(K key);

	Long incrBy(K key, long increment);

	Double incrByFloat(K key, double increment);

	List<V> mget(K... keys);

	Boolean mset(Map<K, V> pairs);

	Long msetnx(Map<K, V> pairs);

	Boolean psetex(K key, long milliseconds, V value);

	String set(K key, V value);

	Boolean set(K key, V value, ExpireOption option, long time, @Nullable SetOption setOption);

	Boolean setbit(K key, long offset, boolean value);

	Boolean setex(K key, long seconds, V value);

	Boolean setNX(K key, V value);

	Long setrange(K key, Long offset, V value);

	Long strlen(K key);
}
