package io.basc.framework.redis;

public interface RedisCommands<K, V>
		extends RedisConnectionCommands<K, V>, RedisGeoCommands<K, V>, RedisHashesCommands<K, V>,
		RedisHyperloglogCommands<K, V>, RedisKeysCommands<K, V>, RedisListsCommands<K, V>, RedisPubSubCommands<K, V>,
		RedisScriptingCommands<K, V>, RedisSortedSetsCommands<K, V>, RedisStreamsCommands<K, V>,
		RedisStringCommands<K, V>, RedisSetsCommands<K, V>, RedisTransactionsCommands<K, V>, RedisServerCommands<K, V> {
}
