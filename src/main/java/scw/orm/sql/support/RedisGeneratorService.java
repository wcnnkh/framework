package scw.orm.sql.support;

import scw.data.RedisDataTemplete;
import scw.data.redis.Redis;
import scw.id.SequenceIdGenerator;
import scw.locks.RedisLockFactory;

public class RedisGeneratorService extends DefaultGeneratorService {

	public RedisGeneratorService(Redis redis) {
		super(new SequenceIdGenerator(redis), new RedisDataTemplete(redis), new RedisLockFactory(redis));
	}
}
