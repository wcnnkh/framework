package scw.utils.id;

import scw.memcached.Memcached;

public final class MemcachedIdFactory implements IdFactory<Long> {
	private final Memcached memcached;

	public MemcachedIdFactory(Memcached memcached) {
		this.memcached = memcached;
	}

	public Long generator(String name) {
		return memcached.incr(this.getClass().getName() + "#" + name, 1, 1);
	}

}
