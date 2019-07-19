package scw.data.redis.jedis;

import redis.clients.jedis.Jedis;
import scw.data.redis.AbstractRedis;
import scw.data.redis.Redis;
import scw.data.redis.RedisOperations;
import scw.data.redis.ResourceManager;
import scw.io.Bytes;

public abstract class AbstractJedisOperations extends AbstractRedis implements Redis, ResourceManager<Jedis> {
	private final RedisOperations<String, String> stringOperations = new JedisStringOperations(this);

	private final RedisOperations<byte[], byte[]> binaryOperations = new JedisBinaryOperations(this) {
		public long incr(byte[] key, long incr, long initValue) {
			return getStringOperations().incr(Bytes.bytes2String(key), incr, initValue);
		};

		public long decr(byte[] key, long decr, long initValue) {
			return getStringOperations().decr(Bytes.bytes2String(key), decr, initValue);
		};
	};

	public RedisOperations<String, String> getStringOperations() {
		return stringOperations;
	}

	public RedisOperations<byte[], byte[]> getBinaryOperations() {
		return binaryOperations;
	}
}
