package scw.data.redis;

import java.util.Arrays;

public abstract class AbstractStringRedisOperations implements RedisOperations<String, String> {
	
	public long incr(String key, long incr, long initValue) {
		return Long.parseLong((String) eval(INCR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(incr), String.valueOf(initValue))));
	}

	public long decr(String key, long decr, long initValue) {
		return Long.parseLong((String) eval(DECR_AND_INIT_SCRIPT, Arrays.asList(key),
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
