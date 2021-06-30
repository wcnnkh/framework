package scw.redis.core.locks;

import java.util.concurrent.TimeUnit;

import scw.context.annotation.Provider;
import scw.locks.LockFactory;
import scw.locks.RenewableLock;
import scw.locks.RenewableLockFactory;
import scw.redis.core.Redis;
import scw.util.XUtils;

@Provider(value = LockFactory.class)
public final class RedisLockFactory extends RenewableLockFactory {
	private final Redis redis;

	public RedisLockFactory(Redis redis) {
		this.redis = redis;
	}

	@Override
	public RenewableLock getLock(String name, TimeUnit timeUnit, long timeout) {
		return new RedisLock(redis, name, XUtils.getUUID(), timeUnit, timeout);
	}
}
