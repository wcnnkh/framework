package scw.memcached.locks;

import java.util.concurrent.TimeUnit;

import scw.data.cas.CAS;
import scw.locks.AbstractLock;
import scw.memcached.Memcached;

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
		boolean b = memcached.add(key, timeout, id);
		if(b){
			autoRenewal(timeout/2, TimeUnit.SECONDS);
		}
		return b;
	}

	public boolean unlock() {
		CAS<String> cas = memcached.getCASOperations().get(key);
		if (id.equals(cas.getValue())) {
			boolean b = memcached.getCASOperations().delete(key, cas.getCas());
			if(b){
				cancelAutoRenewal();
			}
			return b;
		}
		return false;
	}

	public boolean renewal() {
		return renewal(timeout, TimeUnit.SECONDS);
	}

	public boolean renewal(long time, TimeUnit unit) {
		CAS<String> cas = memcached.getCASOperations().get(key);
		if(!id.equals(cas.getValue())){
			return false;
		}
		return memcached.getCASOperations().cas(key, id, (int)unit.toSeconds(time), cas.getCas());
	}
}
