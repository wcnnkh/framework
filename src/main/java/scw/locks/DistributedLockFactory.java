package scw.locks;

import scw.memcached.Memcached;
import scw.redis.Redis;

public final class DistributedLockFactory implements LockFactory {
	private LockFactory lockFactory;

	public DistributedLockFactory(Memcached memcached) {
		this(memcached, 60);
	}

	public DistributedLockFactory(Redis redis) {
		this(redis, 60);
	}

	public DistributedLockFactory(Memcached memcached, int default_timeout) {
		this.lockFactory = new MemcachedLockFactory(memcached, default_timeout);
	}

	public DistributedLockFactory(Redis redis, int default_timeout) {
		this.lockFactory = new RedisLockFactory(redis, default_timeout);
	}

	public Lock getLock(String name, int timeout) {
		return lockFactory.getLock(name, timeout);
	}

	public Lock getLock(String name) {
		return lockFactory.getLock(name);
	}
}
