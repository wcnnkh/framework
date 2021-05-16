package scw.redis.connection;

public interface RedisCommands extends RedisConnectionCommands,
		RedisCusterCommands, RedisGeoCommands, RedisHashesCommands,
		RedisHyperloglogCommands, RedisKeysCommands, RedisListsCommands,
		RedisPubSubCommands, RedisScriptingCommands, RedisSortedSetsCommands,
		RedisStreamsCommands, RedisStringCommands, RedisTransactionsCommands {
}
