package scw.data.redis;

import java.util.Arrays;

public final class RedisUtils {
	private static final String INCR_AND_INIT_SCRIPT = "if redis.call('exists', KEYS[1]) == 1 then return redis.call('incr', KEYS[1], ARGV[1]) else redis.call('set', KEYS[1], ARGV[2]) return ARGV[2] end";
	private static final String DECR_AND_INIT_SCRIPT = "if redis.call('exists', KEYS[1]) == 1 then return redis.call('decr', KEYS[1], ARGV[1]) else redis.call('set', KEYS[1], ARGV[2]) return ARGV[2] end";

	public static String notNullScript(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" ~= nil");
		sb.append(" or (type(").append(name).append(") == 'boolean' and ").append(name).append(")");
		return sb.toString();
	}

	public static String isNullScript(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" == nil");
		sb.append(" or (type(").append(name).append(") == 'boolean' and ").append(name).append(" == false)");
		return sb.toString();
	}

	public static long incr(RedisScriptOperations<String, String> redisScriptOperations, String key, long delta,
			long initialValue) {
		return Long.parseLong((String) redisScriptOperations.eval(INCR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(delta), String.valueOf(initialValue))));
	}

	public static long decr(RedisScriptOperations<String, String> redisScriptOperations, String key, long delta,
			long initialValue) {
		return Long.parseLong((String) redisScriptOperations.eval(DECR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(delta), String.valueOf(initialValue))));
	}
}
