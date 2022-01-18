package io.basc.framework.redis;

public interface RedisPipelineCommands<K, V> extends RedisGeoPipelineCommands<K, V>,
		RedisHashesPipelineCommands<K, V>, RedisHyperloglogPipelineCommands<K, V>, RedisKeysPipelineCommands<K, V>,
		RedisListsPipelineCommands<K, V>, RedisScriptingPipelineCommands<K, V>, RedisSetsPipelineCommands<K, V>,
		RedisStreamsPipelineCommands<K, V>, RedisStringPipelineCommands<K, V> {
}
