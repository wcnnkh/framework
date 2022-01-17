package io.basc.framework.redis;

public interface AsyncRedisCommands<K, V> extends AsyncRedisGeoCommands<K, V>,
		AsyncRedisHashesCommands<K, V>, AsyncRedisHyperloglogCommands<K, V>, AsyncRedisKeysCommands<K, V>,
		AsyncRedisListsCommands<K, V>, AsyncRedisScriptingCommands<K, V>, AsyncRedisSetsCommands<K, V>,
		AsyncRedisStreamsCommands<K, V>, AsyncRedisStringCommands<K, V> {
}
