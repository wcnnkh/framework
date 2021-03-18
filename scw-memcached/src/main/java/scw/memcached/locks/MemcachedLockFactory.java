package scw.memcached.locks;

import java.util.concurrent.TimeUnit;

import scw.context.annotation.Provider;
import scw.locks.LockFactory;
import scw.locks.RenewableLock;
import scw.locks.RenewableLockFactory;
import scw.memcached.Memcached;
import scw.util.XUtils;

@Provider(value = LockFactory.class)
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
