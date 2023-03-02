package io.basc.framework.redis;

import java.util.List;

@SuppressWarnings("unchecked")
public interface RedisTransactionsPipelineCommands<K, V> {
	RedisResponse<String> discard();

	RedisResponse<List<Object>> exec();

	RedisTransaction<K, V> multi();

	RedisResponse<String> unwatch();

	RedisResponse<String> watch(K... keys);
}
