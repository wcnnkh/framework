package io.basc.framework.jedis.test;

import java.util.concurrent.TimeUnit;

import io.basc.framework.jedis.JedisClient;
import io.basc.framework.redis.Redis;
import io.basc.framework.redis.RedisClient;
import io.basc.framework.util.XUtils;
import redis.clients.jedis.JedisPool;

public class ExpireTest {

	public static void main(String[] args) {
		JedisPool jedisPool = new JedisPool("localhost", 6379);
		RedisClient<byte[], byte[]> connectionFactory = new JedisClient(jedisPool);
		String key = "test1";
		Redis redis = new Redis(connectionFactory);
		redis.set(key, XUtils.getUUID(), 1, TimeUnit.SECONDS);
		System.out.println(redis.get(key));
		jedisPool.close();
	}
}
