package io.basc.framework.redis.test;

import io.basc.framework.redis.core.Redis;
import io.basc.framework.redis.core.RedisConnectionFactory;
import io.basc.framework.redis.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPool;

public class RedisTest {
	public static void main(String[] args) {
		JedisPool jedisPool = new JedisPool("localhost", 6379);
		RedisConnectionFactory<byte[], byte[]> connectionFactory = new JedisConnectionFactory(jedisPool);
		Redis redis = new Redis(connectionFactory);
		redis.set("a", "b");
		System.out.println(redis.get("a"));
		redis.del("a");
	}
}
