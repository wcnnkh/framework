package scw.data.memcached;

import scw.data.cas.CASOperations;
import scw.data.cas.CASOperationsWrapper;

public class MemcachedWrapper extends AbstractMemcached implements Memcached {
	private final Memcached memcached;
	private final String prefix;
	private final CASOperations casOperations;

	public MemcachedWrapper(Memcached memcached, String prefix) {
		this.memcached = memcached;
		this.prefix = prefix;
		this.casOperations = new CASOperationsWrapper(memcached.getCASOperations(), prefix);
	}

	public CASOperations getCASOperations() {
		return casOperations;
	}

	@Override
	public Memcached getTargetMemcached() {
		return memcached;
	}

	@Override
	public String getKeyPrefix() {
		return prefix;
	}
}
