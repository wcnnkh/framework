package io.basc.framework.redis;

import java.util.Set;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.page.Pageable;

/**
 * https://redis.io/commands#generic
 * 
 * @author wcnnkh
 *
 */
@SuppressWarnings("unchecked")
public interface RedisKeysPipelineCommands<K, V> {
	RedisResponse<Boolean> copy(K source, K destination, boolean replace);

	RedisResponse<Long> del(K... keys);

	RedisResponse<V> dump(K key);

	RedisResponse<Long> exists(K... keys);

	RedisResponse<Long> expire(K key, long seconds);

	RedisResponse<Long> expireAt(K key, long timestamp);

	RedisResponse<Set<K>> keys(K pattern);

	default RedisResponse<String> migrate(String host, int port, K key, int timeout) {
		return migrate(host, port, timeout, new MigrateParams(), key);
	}

	RedisResponse<String> migrate(String host, int port, int timeout, MigrateParams option, K... keys);

	RedisResponse<Long> objectRefCount(K key);

	RedisResponse<RedisValueEncoding> objectEncoding(K key);

	RedisResponse<Long> objectIdletime(K key);

	RedisResponse<Long> objectFreq(K key);

	RedisResponse<Long> persist(K key);

	RedisResponse<Long> pexpire(K key, long milliseconds);

	RedisResponse<Long> pexpireAt(K key, long timestamp);

	RedisResponse<Long> pttl(K key);

	RedisResponse<K> randomkey();

	RedisResponse<String> rename(K key, K newKey);

	RedisResponse<Long> renamenx(K key, K newKey);

	RedisResponse<String> restore(K key, long ttl, byte[] serializedValue, @Nullable RestoreParams params);

	RedisResponse<Pageable<Long, K>> scan(long cursorId, ScanOptions<K> options);

	RedisResponse<Long> touch(K... keys);

	RedisResponse<Long> ttl(K key);

	RedisResponse<DataType> type(K key);

	RedisResponse<Long> unlink(K... keys);
}
