package scw.data.redis;

import java.util.Collection;
import java.util.Map;

public interface RedisOperations<K, V> extends RedisCommands<K, V> {
	public static final String INCR_AND_INIT_SCRIPT = "if redis.call('exists', KEYS[1]) == 1 then return redis.call('incr', KEYS[1], ARGV[1]) else redis.call('set', KEYS[1], ARGV[2]) return ARGV[2] end";
	public static final String DECR_AND_INIT_SCRIPT = "if redis.call('exists', KEYS[1]) == 1 then return redis.call('decr', KEYS[1], ARGV[1]) else redis.call('set', KEYS[1], ARGV[2]) return ARGV[2] end";

	long incr(K key, long incr, long initValue);

	long decr(K key, long decr, long initValue);

	V getAndTouch(K key, int newExp);

	Map<K, V> mget(Collection<K> keys);
}
