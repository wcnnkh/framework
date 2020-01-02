package scw.data;

import scw.data.memcached.Memcached;

public final class MemcachedDataTemplete extends DataTempleteWrapper {

	public MemcachedDataTemplete(Memcached memcached) {
		super(memcached);
	}
}
