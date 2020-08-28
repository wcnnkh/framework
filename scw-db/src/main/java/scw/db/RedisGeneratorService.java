package scw.db;

import scw.data.generator.SequenceIdGenerator;
import scw.data.locks.RedisLockFactory;
import scw.data.redis.Redis;
import scw.data.redis.RedisDataTemplete;
import scw.sql.orm.support.generation.DefaultGeneratorService;

public class RedisGeneratorService extends DefaultGeneratorService {

	public RedisGeneratorService(Redis redis) {
		super(new SequenceIdGenerator(redis), new RedisDataTemplete(redis), new RedisLockFactory(redis));
	}
}
