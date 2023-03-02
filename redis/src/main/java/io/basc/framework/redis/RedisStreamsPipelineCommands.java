package io.basc.framework.redis;

import java.util.List;

@SuppressWarnings("unchecked")
public interface RedisStreamsPipelineCommands<K, V> {
	RedisResponse<Long> xack(K key, K group, K... ids);

	RedisResponse<List<V>> xclaim(K key, K group, K consumer, long minIdleTime, ClaimArgs args, K... ids);

	RedisResponse<Long> xdel(K key, K... ids);
}
