package io.basc.framework.redis;

import java.util.List;

import io.basc.framework.lang.Nullable;

@SuppressWarnings("unchecked")
public interface RedisScriptingCommands<K, V> {
	@Nullable
	<T> T eval(K script, List<K> keys, List<V> args);

	@Nullable
	<T> T evalsha(K sha1, List<K> keys, List<V> args);

	List<Boolean> scriptexists(K... sha1);

	String scriptFlush();

	String scriptFlush(FlushMode flushMode);

	String scriptKill();

	K scriptLoad(K script);
}
