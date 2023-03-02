package io.basc.framework.redis;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public interface RedisStringPipelineCommands<K, V> {

	RedisResponse<Long> append(K key, V value);

	RedisResponse<Long> bitcount(K key, long start, long end);

	RedisResponse<Long> bitop(BitOP op, K destkey, K... srcKeys);

	RedisResponse<Long> bitpos(K key, boolean bit, Long start, Long end);

	RedisResponse<Long> decr(K key);

	RedisResponse<Long> decrBy(K key, long decrement);

	RedisResponse<V> get(K key);

	RedisResponse<Boolean> getbit(K key, Long offset);

	RedisResponse<V> getdel(K key);

	RedisResponse<V> getEx(K key, ExpireOption option, Long time);

	RedisResponse<V> getrange(K key, long startOffset, long endOffset);

	RedisResponse<V> getset(K key, V value);

	RedisResponse<Long> incr(K key);

	RedisResponse<Long> incrBy(K key, long increment);

	RedisResponse<Double> incrByFloat(K key, double increment);

	RedisResponse<List<V>> mget(K... keys);

	RedisResponse<String> mset(Map<K, V> pairs);

	RedisResponse<Long> msetnx(Map<K, V> pairs);

	RedisResponse<String> psetex(K key, long milliseconds, V value);

	RedisResponse<String> set(K key, V value);

	RedisResponse<String> set(K key, V value, ExpireOption option, long time, SetOption setOption);

	RedisResponse<Boolean> setbit(K key, long offset, boolean value);

	RedisResponse<String> setex(K key, long seconds, V value);

	RedisResponse<Long> setNX(K key, V value);

	RedisResponse<Long> setrange(K key, Long offset, V value);

	RedisResponse<Long> strlen(K key);
}
