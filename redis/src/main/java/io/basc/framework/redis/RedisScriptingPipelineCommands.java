package io.basc.framework.redis;

import java.util.List;

import io.basc.framework.lang.Nullable;

@SuppressWarnings("unchecked")
public interface RedisScriptingPipelineCommands<K, V> {
	@Nullable
	<T> RedisResponse<T> eval(K script, List<K> keys, List<V> args);

	@Nullable
	<T> RedisResponse<T> evalsha(K sha1, List<K> keys, List<V> args);

	RedisResponse<List<Boolean>> scriptexists(K... sha1);

	RedisResponse<String> scriptFlush();

	/**
	 * https://redis.io/commands/script-flush
	 * 
	 * @return Simple string reply
	 */
	RedisResponse<String> scriptFlush(FlushMode flushMode);

	/**
	 * https://redis.io/commands/script-kill
	 * 
	 * @return
	 */
	RedisResponse<String> scriptKill();

	/**
	 * https://redis.io/commands/script-load
	 * 
	 * @param script
	 * @return Bulk string reply This command returns the SHA1 digest of the script
	 *         added into the script cache.
	 */
	RedisResponse<K> scriptLoad(K script);
}
