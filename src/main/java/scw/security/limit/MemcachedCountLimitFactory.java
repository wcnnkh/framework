package scw.security.limit;

import scw.data.memcached.Memcached;

public final class MemcachedCountLimitFactory implements CountLimitFactory {
	private final Memcached memcached;

	public MemcachedCountLimitFactory(Memcached memcached) {
		this.memcached = memcached;
	}

	public CountLimit getCountLimit(CountLimitConfig config) {
		return new MemcachedCountLimit(config, memcached);
	}
}
