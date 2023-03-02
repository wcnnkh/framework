package io.basc.framework.redis;

@SuppressWarnings("unchecked")
public interface RedisHyperloglogPipelineCommands<K, V> {
	RedisResponse<Long> pfadd(K key, V... elements);

	RedisResponse<Long> pfcount(K... keys);

	RedisResponse<String> pfmerge(K destKey, K... sourceKeys);
}
