package scw.data.redis.jedis;

import redis.clients.jedis.Jedis;
import scw.data.redis.AbstractRedis;
import scw.data.redis.Redis;
import scw.data.redis.RedisOperations;
import scw.data.redis.ResourceManager;

public abstract class AbstractJedisOperations extends AbstractRedis implements Redis, ResourceManager<Jedis> {
	private final RedisOperations<String, String> stringOperations = new JedisStringOperations(this);

	private final RedisOperations<byte[], byte[]> binaryOperations = new JedisBinaryOperations(this);

	public RedisOperations<String, String> getStringOperations() {
		return stringOperations;
	}

	public RedisOperations<byte[], byte[]> getBinaryOperations() {
		return binaryOperations;
	}
}
