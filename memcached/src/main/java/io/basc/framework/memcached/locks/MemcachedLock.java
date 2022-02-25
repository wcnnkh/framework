package io.basc.framework.memcached.locks;

import java.util.concurrent.TimeUnit;

import io.basc.framework.data.cas.CAS;
import io.basc.framework.locks.RenewableLock;
import io.basc.framework.memcached.Memcached;

public final class MemcachedLock extends RenewableLock {
	private final Memcached memcached;
	private final String key;
	private final String id;

	public MemcachedLock(Memcached memcached, String key, String id, TimeUnit timeUnit, long timeout) {
		super(timeUnit, timeout);
		this.memcached = memcached;
		this.key = key;
		this.id = id;
	}

	public boolean tryLock() {
		boolean b = memcached.setIfAbsent(key, id, getTimeout(), getTimeUnit());
		if (b) {
			autoRenewal();
		}
		return b;
	}

	public void unlock() {
		cancelAutoRenewal();
		CAS<Object> cas = memcached.gets(key);
		if (id.equals(cas.getValue())) {
			memcached.delete(key, cas.getCas());
		}
	}

	public boolean renewal(long time, TimeUnit unit) {
		CAS<Object> cas = memcached.gets(key);
		if (!id.equals(cas.getValue())) {
			return false;
		}
		return memcached.cas(key, id, cas.getCas(), time, unit);
	}
}
