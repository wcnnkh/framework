package shuchaowen.memcached;

import shuchaowen.core.util.XUtils;

public class MemcachedLock {
	private final Memcached memcached;
	private final String key;
	private final String id;
	private final int timeout;

	public MemcachedLock(Memcached memcached, String key) {
		this(memcached, key, XUtils.getUUID(), 30);// 默认30秒过期
	}

	public MemcachedLock(Memcached memcached, String key, String id, int timeout) {
		this.memcached = memcached;
		this.key = key;
		this.id = id;
		this.timeout = timeout;
	}

	/**
	 * 尝试获取锁，会立刻得到结果
	 * 
	 * @return
	 */
	public boolean lock() {
		return memcached.add(key, timeout, id);
	}

	/**
	 * 尝试获取锁，如果无法获取会一直阻塞直到获取到锁
	 */
	public void lockWait(int sleep) throws InterruptedException {
		while (!lock()) {
			Thread.sleep(sleep);
		}
	}

	/**
	 * @return 返回值是可以忽略的，如果返回fasle可能是key已经失效或已经解锁
	 */
	public boolean unLock() {
		CAS<String> cas = memcached.gets(key);
		if (id.equals(cas.getValue())) {
			return memcached.delete(key, cas.getCas(), 1000L);
		}
		return false;
	}
}
