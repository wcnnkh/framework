package scw.locks;

import scw.core.utils.XUtils;
import scw.data.memcached.CAS;
import scw.data.memcached.Memcached;

public final class MemcachedLock extends AbstractLock {
	private final Memcached memcached;
	private final String key;
	private final String id;
	private final int timeout;

	public MemcachedLock(Memcached memcached, String key) {
		this(memcached, key, XUtils.getUUID(), 60);// 默认60秒过期
	}

	public MemcachedLock(Memcached memcached, String key, String id, int timeout) {
		this.memcached = memcached;
		this.key = key;
		this.id = id;
		this.timeout = timeout;
	}

	public boolean lock() {
		return memcached.add(key, timeout, id);
	}

	public void unlock() {
		CAS<Object> cas = memcached.gets(key);
		if (id.equals(cas.getValue())) {
			memcached.delete(key, cas.getCas(), 1000L);
		}
	}
}
