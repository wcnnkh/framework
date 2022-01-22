package io.basc.framework.redis;

import io.basc.framework.lang.Nullable;

import java.util.List;

@SuppressWarnings("unchecked")
public interface RedisScriptingCommands<K, V> {
	@Nullable
	<T> T eval(K script, List<K> keys, List<V> args);

	@Nullable
	<T> T evalsha(K sha1, List<K> keys, List<V> args);

	List<Boolean> scriptexists(K... sha1);

	String scriptFlush();

	/**
	 * https://redis.io/commands/script-flush
	 * 
	 * @return Simple string reply
	 */
	String scriptFlush(FlushMode flushMode);

	/**
	 * https://redis.io/commands/script-kill
	 * 
	 * @return
	 */
	String scriptKill();

	/**
	 * https://redis.io/commands/script-load
	 * 
	 * @param script
	 * @return Bulk string reply This command returns the SHA1 digest of the script
	 *         added into the script cache.
	 */
	K scriptLoad(K script);
}
