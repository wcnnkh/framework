package io.basc.framework.redis;

public interface RedisConnectionCommands<K, V> {
	V ping(K message);

	String select(int index);
}
