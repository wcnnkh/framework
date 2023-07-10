package io.basc.framework.redis;

import java.util.Set;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.page.Cursor;

/**
 * https://redis.io/commands#generic
 * 
 * @author wcnnkh
 *
 */
@SuppressWarnings("unchecked")
public interface RedisKeysCommands<K, V> {
	Boolean copy(K source, K destination, @Nullable Integer destinationDB, boolean replace);

	Long del(K... keys);

	V dump(K key);

	Long exists(K... keys);

	Long expire(K key, long seconds);
	Long expireAt(K key, long timestamp);

	Set<K> keys(K pattern);

	String migrate(String host, int port, K key, int targetDB, int timeout);

	String migrate(String host, int port, int targetDB, int timeout, MigrateParams option, K... keys);

	Long move(K key, int targetDB);

	Long objectRefCount(K key);

	RedisValueEncoding objectEncoding(K key);

	Long objectIdletime(K key);

	Long objectFreq(K key);

	Long persist(K key);

	Long pexpire(K key, long milliseconds);

	Long pexpireAt(K key, long timestamp);

	Long pttl(K key);

	K randomkey();

	String rename(K key, K newKey);

	Boolean renamenx(K key, K newKey);

	String restore(K key, long ttl, byte[] serializedValue, @Nullable RestoreParams params);

	Cursor<Long, K> scan(long cursorId, ScanOptions<K> options);

	Long touch(K... keys);

	Long ttl(K key);

	DataType type(K key);

	Long unlink(K... keys);

	Long wait(int numreplicas, long timeout);
}
