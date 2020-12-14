package scw.sql.orm.support.generation;

import scw.core.instance.annotation.SPI;
import scw.data.generator.SequenceIdGenerator;
import scw.redis.Redis;
import scw.redis.RedisDataTemplete;
import scw.redis.locks.RedisLockFactory;

@SPI(order=Integer.MIN_VALUE)
public class RedisGeneratorService extends DefaultGeneratorService {

	public RedisGeneratorService(Redis redis) {
		super(new SequenceIdGenerator(redis), new RedisDataTemplete(redis), new RedisLockFactory(redis));
	}
}
