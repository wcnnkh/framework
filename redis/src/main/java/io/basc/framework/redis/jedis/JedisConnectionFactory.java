package io.basc.framework.redis.jedis;

import io.basc.framework.redis.core.RedisConnection;
import io.basc.framework.redis.core.RedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisConnectionFactory implements RedisConnectionFactory<byte[], byte[]> {
	private final JedisPool jedisPool;

	public JedisConnectionFactory(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public RedisConnection<byte[], byte[]> getConnection() {
		Jedis jedis = jedisPool.getResource();
		return new JedisConnection(jedis);
	}

}
