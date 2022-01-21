package io.basc.framework.jedis.test;

import io.basc.framework.jedis.JedisClient;
import io.basc.framework.redis.Redis;
import io.basc.framework.redis.RedisClient;
import redis.clients.jedis.JedisPool;

public class JedisTest {
	public static void main(String[] args) {
		JedisPool jedisPool = new JedisPool("localhost", 6379);
		RedisClient<byte[], byte[]> connectionFactory = new JedisClient(jedisPool);
		Redis redis = new Redis(connectionFactory);
		redis.set("a", "b");
		System.out.println(redis.get("a"));
		redis.del("a");

		for(int i=0 ;i<10; i++) {
			System.out.println(redis.incr("v", 1, 10, 0));
		}
	}
}
