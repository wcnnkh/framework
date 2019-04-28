package scw.data.redis.jedis.cluster;

import redis.clients.jedis.JedisCluster;
import scw.data.redis.Redis;
import scw.data.redis.RedisOperations;
import scw.data.redis.ResourceManager;

public abstract class AbstractClusterOperations implements Redis, ResourceManager<JedisCluster> {
	private final RedisOperations<String, String> stringCommands = new AbstractClusterStringOperations() {

		public JedisCluster getResource() {
			return getResource();
		}

		public void close(JedisCluster resource) {
			close(resource);
		}
	};

	private final RedisOperations<byte[], byte[]> binaryOperations = new AbstractClusterBinaryOperations() {

		public JedisCluster getResource() {
			return getResource();
		}

		public void close(JedisCluster resource) {
			close(resource);
		}
	};

	public RedisOperations<String, String> getStringOperations() {
		return stringCommands;
	}

	public RedisOperations<byte[], byte[]> getBinaryOperations() {
		return binaryOperations;
	}

}
