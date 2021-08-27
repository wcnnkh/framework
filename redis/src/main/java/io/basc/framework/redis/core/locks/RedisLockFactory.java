package io.basc.framework.redis.core.locks;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.locks.LockFactory;
import io.basc.framework.locks.RenewableLock;
import io.basc.framework.locks.RenewableLockFactory;
import io.basc.framework.redis.core.Redis;
import io.basc.framework.util.XUtils;

import java.util.concurrent.TimeUnit;

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
