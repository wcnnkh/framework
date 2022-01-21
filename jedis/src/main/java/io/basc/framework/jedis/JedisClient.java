package io.basc.framework.jedis;

import io.basc.framework.redis.RedisConnection;
import io.basc.framework.redis.RedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisClient implements RedisClient<byte[], byte[]> {
	private final JedisPool jedisPool;

	public JedisClient(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public RedisConnection<byte[], byte[]> getConnection() {
		Jedis jedis = jedisPool.getResource();
		return new JedisConnection(jedis);
	}

}
