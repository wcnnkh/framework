package scw.utils.locks;

import scw.common.utils.XUtils;
import scw.memcached.Memcached;

public final class MemcachedLockFactory extends AbstractLockFactory {
	private final Memcached memcached;

	public MemcachedLockFactory(Memcached memcached, int default_timeout) {
		super(default_timeout);
		this.memcached = memcached;
	}

	public Lock getLock(String name, int timeout) {
		return new MemcachedLock(memcached, name, XUtils.getUUID(), timeout);
	}
}
