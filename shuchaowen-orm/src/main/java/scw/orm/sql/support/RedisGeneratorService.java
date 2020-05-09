package scw.orm.sql.support;

import scw.data.locks.RedisLockFactory;
import scw.data.redis.Redis;
import scw.data.redis.RedisDataTemplete;
import scw.generator.id.SequenceIdGenerator;

public class RedisGeneratorService extends DefaultGeneratorService {

	public RedisGeneratorService(Redis redis) {
		super(new SequenceIdGenerator(redis), new RedisDataTemplete(redis), new RedisLockFactory(redis));
	}
}
