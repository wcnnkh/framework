package io.basc.framework.redis;

@FunctionalInterface
public interface RedisCallback<K, V, T> {
	T doInRedis(RedisCommands<K, V> commands) throws RedisSystemException;
}
