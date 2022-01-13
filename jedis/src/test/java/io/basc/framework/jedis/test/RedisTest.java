package io.basc.framework.jedis.test;

import io.basc.framework.jedis.JedisConnectionFactory;
import io.basc.framework.redis.Redis;
import io.basc.framework.redis.RedisConnectionFactory;
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
