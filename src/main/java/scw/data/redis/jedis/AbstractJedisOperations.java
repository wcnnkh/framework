package scw.data.redis.jedis;

import redis.clients.jedis.Jedis;
import scw.data.redis.Redis;
import scw.data.redis.RedisOperations;

public abstract class AbstractJedisOperations implements Redis, ResourceManager<Jedis> {
	private final RedisOperations<String, String> stringOperations = new AbstractJedisStringOperations() {

		public Jedis getResource() {
			return getResource();
		}

		public void close(Jedis resource) {
			close(resource);
		}
	};

	private final RedisOperations<byte[], byte[]> binaryOperations = new AbstractJedisBinaryOperations() {

		public Jedis getResource() {
			return getResource();
		}

		public void close(Jedis resource) {
			close(resource);
		}
	};

	public RedisOperations<String, String> getStringOperations() {
		return stringOperations;
	}

	public RedisOperations<byte[], byte[]> getBinaryOperations() {
		return binaryOperations;
	}

}
