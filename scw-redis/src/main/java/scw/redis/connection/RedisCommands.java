package scw.redis.connection;

public interface RedisCommands<K, V> extends RedisConnectionCommands<K, V>, RedisGeoCommands<K, V>, RedisHashesCommands<K, V>,
		RedisHyperloglogCommands<K, V>, RedisKeysCommands<K, V>, RedisListsCommands<K, V>,
		RedisPubSubCommands, RedisScriptingCommands, RedisSortedSetsCommands<K, V>,
		RedisStreamsCommands, RedisStringCommands<K, V>, RedisTransactionsCommands {
}
