package scw.sql.orm.support.generation;

import scw.context.annotation.Provider;
import scw.data.generator.SequenceIdGenerator;
import scw.redis.Redis;
import scw.redis.RedisDataOperations;
import scw.redis.locks.RedisLockFactory;

@Provider
public class RedisGeneratorService extends DefaultGeneratorService {

	public RedisGeneratorService(Redis redis) {
		super(new SequenceIdGenerator(redis), new RedisDataOperations(redis), new RedisLockFactory(redis));
	}
}
