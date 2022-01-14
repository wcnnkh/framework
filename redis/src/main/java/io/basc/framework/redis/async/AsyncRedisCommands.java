package io.basc.framework.redis.async;

public interface AsyncRedisCommands<K, V> extends AsyncRedisConnectionCommands<K, V>, AsyncRedisGeoCommands<K, V>,
		AsyncRedisHashesCommands<K, V>, AsyncRedisHyperloglogCommands<K, V>, AsyncRedisKeysCommands<K, V>,
		AsyncRedisListsCommands<K, V>, AsyncRedisScriptingCommands<K, V>, AsyncRedisSetsCommands<K, V>,
		AsyncRedisStreamsCommands<K, V>, AsyncRedisStringCommands<K, V> {
}
