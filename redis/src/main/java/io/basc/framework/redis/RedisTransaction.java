package io.basc.framework.redis;

public interface RedisTransaction<K, V> extends RedisPipelineCommands<K, V>, RedisTransactionsCommands<K, V> {
	boolean isAlive();
}
