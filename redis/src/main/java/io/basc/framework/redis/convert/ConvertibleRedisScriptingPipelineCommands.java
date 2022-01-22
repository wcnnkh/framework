package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisScriptingPipelineCommands;

public interface ConvertibleRedisScriptingPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisScriptingPipelineCommands<K, V> {

	RedisScriptingPipelineCommands<SK, SV> getSourceRedisScriptingCommands();

	@Override
	default <T> RedisResponse<T> eval(K script, List<K> keys, List<V> args) {
		SK k = getKeyCodec().encode(script);
		List<SK> ks = getKeyCodec().encode(keys);
		List<SV> vs = getValueCodec().encode(args);
		return getSourceRedisScriptingCommands().eval(k, ks, vs);
	}

	@Override
	default <T> RedisResponse<T> evalsha(K sha1, List<K> keys, List<V> args) {
		SK k = getKeyCodec().encode(sha1);
		List<SK> ks = getKeyCodec().encode(keys);
		List<SV> vs = getValueCodec().encode(args);
		return getSourceRedisScriptingCommands().evalsha(k, ks, vs);
	}
}
