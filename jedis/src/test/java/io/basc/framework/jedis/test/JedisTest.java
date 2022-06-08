package io.basc.framework.jedis.test;

import java.util.List;

import io.basc.framework.data.geo.Marker;
import io.basc.framework.jedis.JedisClient;
import io.basc.framework.redis.Message;
import io.basc.framework.redis.MessageListener;
import io.basc.framework.redis.Redis;
import io.basc.framework.redis.RedisClient;
import io.basc.framework.redis.RedisConnection;
import io.basc.framework.redis.RedisLbs;
import io.basc.framework.redis.RedisPipeline;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.util.XUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class JedisTest {
	public static void main(String[] args) {
		JedisPool jedisPool = new JedisPool("localhost", 6379);

		Jedis jedis = jedisPool.getResource();

		Pipeline pattern = jedis.pipelined();
		Response<Long> res = pattern.incr("v");
		List<Object> ress = pattern.syncAndReturnAll();
		System.out.println(ress);
		System.out.println(res.get());
		jedis.close();
		System.out.println("--------------");
		RedisClient<byte[], byte[]> connectionFactory = new JedisClient(jedisPool);
		Redis redis = new Redis(connectionFactory);
		System.out.println("----redis-info-----");
		System.out.println(redis.info());
		System.out.println("----redis-info------");
		redis.set("a", "b");
		System.out.println("----memoryUsage-----");
		System.out.println(redis.memoryUsage("a"));
		System.out.println("----memoryUsage------");
		System.out.println(redis.get("a"));
		redis.del("a");

		for (int i = 0; i < 10; i++) {
			System.out.println(redis.incr("v", 1, 10));
		}

		Long value = redis.del("v");
		System.out.println("del:" + value);

		RedisConnection<String, String> connection = redis.getConnection();
		RedisPipeline<String, String> pipeline = connection.pipelined();
		RedisResponse<Long> response1 = pipeline.incr("v");
		RedisResponse<Long> response2 = pipeline.incr("v");
		List<Object> pipelineResponses = pipeline.exec();
		System.out.println("pres1:" + response1.get());
		System.out.println("pres2:" + response2.get());
		System.out.println("press:" + pipelineResponses);

		RedisLbs<String, String> lbs = new RedisLbs<String, String>(redis, "aaa");
		lbs.report(new Marker<String>("a", 1, 1));
		Marker<String> marker = lbs.getMarker("a");
		System.out.println(marker);
		connection.close();

		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
				}
				redis.publish("sub-test", XUtils.getUUID());
			}
		}).start();

		connection = redis.getConnection();
		connection.subscribe(new MessageListener<String, String>() {

			@Override
			public void onMessage(Message<String, String> message, String pattern) {
				System.out.println("listener:" + message.getBody());
			}
		}, "sub-test");
	}
}
