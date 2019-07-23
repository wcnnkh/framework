package scw.data.redis.jedis.cluster;

import redis.clients.jedis.JedisCluster;
import scw.data.redis.AbstractRedis;
import scw.data.redis.Redis;
import scw.data.redis.RedisOperations;
import scw.data.redis.ResourceManager;
import scw.io.Bytes;

public abstract class AbstractClusterOperations extends AbstractRedis implements Redis, ResourceManager<JedisCluster> {
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

		public long incr(byte[] key, long incr, long initValue) {
			return getStringOperations().incr(Bytes.bytes2String(key), incr, initValue);
		};

		public long decr(byte[] key, long decr, long initValue) {
			return getStringOperations().decr(Bytes.bytes2String(key), decr, initValue);
		};
	};

	public RedisOperations<String, String> getStringOperations() {
		return stringCommands;
	}

	public RedisOperations<byte[], byte[]> getBinaryOperations() {
		return binaryOperations;
	}

}
