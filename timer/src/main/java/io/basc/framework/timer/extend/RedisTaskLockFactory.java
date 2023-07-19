package io.basc.framework.timer.extend;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.Ordered;
import io.basc.framework.redis.Redis;
import io.basc.framework.redis.cas.RedisCASOperations;
import io.basc.framework.timer.support.CASTaskLockFactory;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE - 1)
public class RedisTaskLockFactory extends CASTaskLockFactory {

	public RedisTaskLockFactory(Redis redis) {
		super(new RedisCASOperations(redis));
	}

}
