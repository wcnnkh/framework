package io.basc.framework.memcached.locks;

import io.basc.framework.data.cas.CAS;
import io.basc.framework.locks.RenewableLock;
import io.basc.framework.memcached.Memcached;

import java.util.concurrent.TimeUnit;

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
		boolean b = memcached.add(key, (int)getTimeout(TimeUnit.SECONDS), id);
		if(b){
			autoRenewal();
		}
		return b;
	}

	public void unlock() {
		cancelAutoRenewal();
		CAS<String> cas = memcached.getCASOperations().get(key);
		if (id.equals(cas.getValue())) {
			memcached.getCASOperations().delete(key, cas.getCas());
		}
	}

	public boolean renewal(long time, TimeUnit unit) {
		CAS<String> cas = memcached.getCASOperations().get(key);
		if(!id.equals(cas.getValue())){
			return false;
		}
		return memcached.getCASOperations().cas(key, id, (int)unit.toSeconds(time), cas.getCas());
	}
}
