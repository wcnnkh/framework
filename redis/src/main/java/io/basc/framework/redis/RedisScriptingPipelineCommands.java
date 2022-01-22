package io.basc.framework.redis;

import java.util.List;

import io.basc.framework.lang.Nullable;

public interface RedisScriptingPipelineCommands<K, V> {
	@Nullable
	<T> RedisResponse<T> eval(K script, List<K> keys, List<V> args);

	@Nullable
	<T> RedisResponse<T> evalsha(K sha1, List<K> keys, List<V> args);
}
