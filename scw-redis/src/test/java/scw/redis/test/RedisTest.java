package scw.redis.test;

import redis.clients.jedis.JedisPool;
import scw.redis.core.Redis;
import scw.redis.core.RedisConnectionFactory;
import scw.redis.jedis.JedisConnectionFactory;

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
