package io.basc.framework.redis;

import java.util.List;

@SuppressWarnings("unchecked")
public interface RedisStreamsCommands<K, V> {
	Long xack(K key, K group, K... ids);

	List<V> xclaim(K key, K group, K consumer, long minIdleTime, ClaimArgs args, K... ids);

	Long xdel(K key, K... ids);
}
