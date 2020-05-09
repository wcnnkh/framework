package scw.orm.sql.support;

import scw.data.redis.Redis;
import scw.data.redis.RedisDataTemplete;
import scw.generator.id.SequenceIdGenerator;
import scw.locks.RedisLockFactory;

public class RedisGeneratorService extends DefaultGeneratorService {

	public RedisGeneratorService(Redis redis) {
		super(new SequenceIdGenerator(redis), new RedisDataTemplete(redis), new RedisLockFactory(redis));
	}
}
