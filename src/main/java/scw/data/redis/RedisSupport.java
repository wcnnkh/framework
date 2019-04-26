package scw.data.redis;

public interface RedisSupport {
	long incr(String key, long incr, long initValue);

	long decr(String key, long decr, long initValue);
}
