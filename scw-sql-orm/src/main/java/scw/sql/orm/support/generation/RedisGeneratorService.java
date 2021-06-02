package scw.sql.orm.support.generation;

import scw.context.annotation.Provider;
import scw.data.generator.SequenceIdGenerator;
import scw.redis.core.Redis;
import scw.redis.core.locks.RedisLockFactory;

@Provider
public class RedisGeneratorService extends DefaultGeneratorService {

	public RedisGeneratorService(Redis redis) {
		super(new SequenceIdGenerator(redis.getDataOperations()), redis.getDataOperations(), new RedisLockFactory(redis));
	}
}
