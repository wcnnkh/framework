package io.basc.framework.redis;

@SuppressWarnings("unchecked")
public interface RedisHyperloglogCommands<K, V> {
	Long pfadd(K key, V... elements);

	Long pfcount(K... keys);

	String pfmerge(K destKey, K... sourceKeys);
}
