package run.soeasy.framework.redis.sequences;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import run.soeasy.framework.core.RandomUtils;

public class RedissonSequenceTest {
	//@Test
	public void test() {
		Config config = new Config();
		config.useSingleServer().setAddress("redis://localhost:6379");
		RedissonClient redissonClient = Redisson.create(config);
		try {
			int initValue = 10;
			RedissonSequence sequence = new RedissonSequence("test_sequence_" + RandomUtils.uuid(), () -> initValue,
					redissonClient);
			Long value = sequence.next();
			System.out.println("序列值: " + value);
			assert value == initValue + 1;
		} finally {
			redissonClient.shutdown();
		}
	}
}
