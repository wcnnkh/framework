package run.soeasy.framework.redis.sequences;

import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import run.soeasy.framework.core.RandomUtils;

public class StringRedisTemplateSequenceTest {

	private LettuceConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
		config.setPassword(RedisPassword.none());
		config.setDatabase(1);
		return new LettuceConnectionFactory(config);
	}

	// @Test
	public void test() {
		LettuceConnectionFactory connectionFactory = redisConnectionFactory();
		try {
			connectionFactory.afterPropertiesSet();
			StringRedisTemplate redisTemplate = new StringRedisTemplate(connectionFactory);
			redisTemplate.afterPropertiesSet();
			int initValue = 10;
			StringRedisTemplateSequence sequence = new StringRedisTemplateSequence(
					"test_sequence_" + RandomUtils.uuid(), () -> initValue, redisTemplate);
			long value = sequence.next();
			System.out.println("序列值: " + value);
			assert value == initValue + 1;
		} finally {
			connectionFactory.destroy();
		}
	}
}
