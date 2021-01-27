package scw.redis.locks;

import java.util.concurrent.TimeUnit;

import scw.context.annotation.Provider;
import scw.locks.AbstractLockFactory;
import scw.locks.Lock;
import scw.locks.LockFactory;
import scw.redis.Redis;
import scw.util.XUtils;

@Provider(order = Integer.MIN_VALUE + 1, value = LockFactory.class)
public final class RedisLockFactory extends AbstractLockFactory {
	private final Redis redis;

	public RedisLockFactory(Redis redis) {
		this.redis = redis;
	}

	public Lock getLock(String name, long timeout, TimeUnit timeUnit) {
		return new RedisLock(redis, name, XUtils.getUUID(), (int) TimeUnit.SECONDS.convert(timeout, timeUnit));
	}

}
