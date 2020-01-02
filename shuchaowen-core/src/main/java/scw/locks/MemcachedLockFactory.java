package scw.locks;

import java.util.concurrent.TimeUnit;

import scw.core.utils.XUtils;
import scw.data.memcached.Memcached;

public final class MemcachedLockFactory extends AbstractLockFactory {
	private final Memcached memcached;

	public MemcachedLockFactory(Memcached memcached) {
		this.memcached = memcached;
	}

	public Lock getLock(String name, long timeout, TimeUnit timeUnit) {
		return new MemcachedLock(memcached, name, XUtils.getUUID(), (int) TimeUnit.SECONDS.convert(timeout, timeUnit));
	}
}
