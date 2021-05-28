package scw.redis.connection;

public interface RedisCommands extends RedisConnectionCommands,
		RedisCusterCommands, RedisGeoCommands, RedisHashesCommands,
		RedisHyperloglogCommands, RedisKeysCommands<byte[], byte[]>, RedisListsCommands,
		RedisPubSubCommands, RedisScriptingCommands, RedisSortedSetsCommands,
		RedisStreamsCommands, RedisStringCommands, RedisTransactionsCommands {
}
