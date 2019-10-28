package scw.data.redis.operations;

import java.util.Arrays;

import scw.data.redis.RedisOperations;

public abstract class AbstractStringRedisOperations implements RedisOperations<String, String> {
	private static final String INCR_AND_INIT_SCRIPT = "if redis.call('exists', KEYS[1]) == 1 then return redis.call('incr', KEYS[1], ARGV[1]) else redis.call('set', KEYS[1], ARGV[2]) return ARGV[2] end";
	private static final String DECR_AND_INIT_SCRIPT = "if redis.call('exists', KEYS[1]) == 1 then return redis.call('decr', KEYS[1], ARGV[1]) else redis.call('set', KEYS[1], ARGV[2]) return ARGV[2] end";
	
	public long incr(String key, long incr, long initValue) {
		return Long.parseLong((String)eval(INCR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(incr), String.valueOf(initValue))));
	}

	public long decr(String key, long decr, long initValue) {
		return Long.parseLong((String)eval(DECR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(decr), String.valueOf(initValue))));
	}

	public String getAndTouch(String key, int newExp) {
		String v = get(key);
		if (v != null) {
			expire(key, newExp);
		}
		return v;
	}
	
	
}
