package scw.beans.plugins.cache;

import scw.memcached.Memcached;

public final class MemcachedCacheFilter extends AbstractCacheFilter {
	private final Memcached memcached;

	public MemcachedCacheFilter(Memcached memcached, boolean debug) {
		super(debug);
		this.memcached = memcached;
	}

	@Override
	protected <T> T getCache(String key, Class<T> type) {
		return memcached.get(key);
	}

	@Override
	protected void setCache(String key, int exp, Class<?> type, Object data) {
		memcached.set(key, exp * 2, data);
	}

}
