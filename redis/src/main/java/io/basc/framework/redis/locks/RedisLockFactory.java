package io.basc.framework.redis.locks;

import java.util.concurrent.TimeUnit;

import io.basc.framework.locks.RenewableLock;
import io.basc.framework.locks.RenewableLockFactory;
import io.basc.framework.redis.Redis;
import io.basc.framework.util.XUtils;

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
