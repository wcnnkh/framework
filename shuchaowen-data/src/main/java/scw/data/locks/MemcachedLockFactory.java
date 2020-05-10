package scw.data.locks;

import java.util.concurrent.TimeUnit;

import scw.core.instance.annotation.Configuration;
import scw.core.utils.XUtils;
import scw.data.memcached.Memcached;
import scw.locks.AbstractLockFactory;
import scw.locks.Lock;

@Configuration(order=Integer.MIN_VALUE + 1)
public final class MemcachedLockFactory extends AbstractLockFactory {
	private final Memcached memcached;

	public MemcachedLockFactory(Memcached memcached) {
		this.memcached = memcached;
	}

	public Lock getLock(String name, long timeout, TimeUnit timeUnit) {
		return new MemcachedLock(memcached, name, XUtils.getUUID(), (int) TimeUnit.SECONDS.convert(timeout, timeUnit));
	}
}
