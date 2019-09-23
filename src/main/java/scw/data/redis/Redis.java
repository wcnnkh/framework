package scw.data.redis;

import scw.beans.annotation.AutoImpl;
import scw.data.cas.CASOperations;

@AutoImpl(className = "scw.data.redis.jedis.RedisByJedisPool")
public interface Redis {
	RedisOperations<String, String> getStringOperations();

	RedisOperations<byte[], byte[]> getBinaryOperations();

	RedisOperations<String, Object> getObjectOperations();

	<T> RedisOperations<String, T> getSpecifiedTypeOperations(Class<T> type);

	CASOperations getCASOperations();
}
