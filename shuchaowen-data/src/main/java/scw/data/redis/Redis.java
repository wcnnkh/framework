package scw.data.redis;

import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Bean;
import scw.data.cas.CASOperations;

@AutoImpl(className = "scw.data.redis.jedis.RedisByJedisPool")
@Bean("redis")
public interface Redis {
	RedisOperations<String, String> getStringOperations();

	RedisOperations<byte[], byte[]> getBinaryOperations();

	RedisOperations<String, Object> getObjectOperations();

	CASOperations getCASOperations();
}
