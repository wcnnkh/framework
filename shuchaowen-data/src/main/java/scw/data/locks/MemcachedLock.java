package scw.data.locks;

import scw.data.cas.CAS;
import scw.data.memcached.Memcached;
import scw.locks.AbstractLock;

public final class MemcachedLock extends AbstractLock {
	private final Memcached memcached;
	private final String key;
	private final String id;
	private final int timeout;

	public MemcachedLock(Memcached memcached, String key, String id, int timeout) {
		this.memcached = memcached;
		this.key = key;
		this.id = id;
		this.timeout = timeout;
	}

	public boolean tryLock() {
		return memcached.add(key, timeout, id);
	}

	public void unlock() {
		CAS<String> cas = memcached.getCASOperations().get(key);
		if (id.equals(cas.getValue())) {
			memcached.getCASOperations().delete(key, cas.getCas());
		}
	}
}
