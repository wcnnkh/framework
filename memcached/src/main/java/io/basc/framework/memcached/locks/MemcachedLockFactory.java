package io.basc.framework.memcached.locks;

import java.util.concurrent.TimeUnit;

import io.basc.framework.locks.RenewableLock;
import io.basc.framework.locks.RenewableLockFactory;
import io.basc.framework.memcached.Memcached;
import io.basc.framework.util.XUtils;

public final class MemcachedLockFactory extends RenewableLockFactory {
	private final Memcached memcached;

	public MemcachedLockFactory(Memcached memcached) {
		this.memcached = memcached;
	}

	@Override
	public RenewableLock getLock(String name, TimeUnit timeUnit, long timeout) {
		return new MemcachedLock(memcached, name, XUtils.getUUID(), timeUnit, timeout);
	}
}
