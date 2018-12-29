package scw.locks;

import scw.common.utils.XUtils;
import scw.memcached.Memcached;
import scw.redis.Redis;

public final class DistributedLock extends AbstractLock {
	private Lock lock;

	public DistributedLock(Memcached memcached, String key) {
		this(memcached, key, XUtils.getUUID(), 60);// 默认60秒过期
	}

	public DistributedLock(Redis redis, String key) {
		this(redis, key, XUtils.getUUID(), 60);
	}

	public DistributedLock(Memcached memcached, String key, String id, int timeout) {
		this.lock = new MemcachedLock(memcached, key, id, timeout);
	}

	public DistributedLock(Redis redis, String key, String id, int timeout) {
		this.lock = new RedisLock(redis, key, id, timeout);
	}

	public boolean lock() {
		return lock.lock();
	}

	public void unlock() {
		lock.unlock();
	}
}
