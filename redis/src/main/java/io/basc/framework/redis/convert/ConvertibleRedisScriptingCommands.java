package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.FlushMode;
import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisScriptingCommands;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisScriptingCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisScriptingCommands<K, V> {

	RedisScriptingCommands<SK, SV> getSourceRedisScriptingCommands();

	@Override
	default <T> T eval(K script, List<K> keys, List<V> args) {
		SK k = getKeyCodec().encode(script);
		List<SK> ks = getKeyCodec().encode(keys);
		List<SV> vs = getValueCodec().encode(args);
		return getSourceRedisScriptingCommands().eval(k, ks, vs);
	}

	@Override
	default <T> T evalsha(K sha1, List<K> keys, List<V> args) {
		SK k = getKeyCodec().encode(sha1);
		List<SK> ks = getKeyCodec().encode(keys);
		List<SV> vs = getValueCodec().encode(args);
		return getSourceRedisScriptingCommands().evalsha(k, ks, vs);
	}

	@Override
	default List<Boolean> scriptexists(K... sha1) {
		SK[] ks = getKeyCodec().encode(sha1);
		return getSourceRedisScriptingCommands().scriptexists(ks);
	}

	@Override
	default String scriptFlush() {
		return getSourceRedisScriptingCommands().scriptFlush();
	}

	@Override
	default String scriptFlush(FlushMode flushMode) {
		return getSourceRedisScriptingCommands().scriptFlush(flushMode);
	}

	@Override
	default String scriptKill() {
		return getSourceRedisScriptingCommands().scriptKill();
	}

	@Override
	default K scriptLoad(K script) {
		SK k = getKeyCodec().encode(script);
		SK v = getSourceRedisScriptingCommands().scriptLoad(k);
		return getKeyCodec().decode(v);
	}
}
