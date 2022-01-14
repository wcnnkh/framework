package io.basc.framework.redis.async;

import java.util.List;

import io.basc.framework.lang.Nullable;
import io.basc.framework.redis.FlushMode;

@SuppressWarnings("unchecked")
public interface AsyncRedisScriptingCommands<K, V> {
	@Nullable
	<T> Response<T> eval(K script, List<K> keys, List<V> args);

	@Nullable
	<T> Response<T> evalsha(K sha1, List<K> keys, List<V> args);

	Response<List<Boolean>> scriptexists(K... sha1);

	Response<String> scriptFlush();

	/**
	 * https://redis.io/commands/script-flush
	 * 
	 * @return Simple string reply
	 */
	Response<String> scriptFlush(FlushMode flushMode);

	/**
	 * https://redis.io/commands/script-kill
	 * 
	 * @return
	 */
	Response<String> scriptKill();

	/**
	 * https://redis.io/commands/script-load
	 * 
	 * @param script
	 * @return Bulk string reply This command returns the SHA1 digest of the script
	 *         added into the script cache.
	 */
	Response<K> scriptLoad(K script);
}
