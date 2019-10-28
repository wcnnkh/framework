package scw.data.cache.memory;

import scw.core.exception.NotSupportException;
import scw.data.cache.AbstractMemoryCache;

public class DefaultMemoryCache extends AbstractMemoryCache {
	private volatile Object value;

	public DefaultMemoryCache(Object value) {
		this.value = value;
	}

	public long incr(long incr, long initialValue) {
		throw new NotSupportException("incr");
	}

	public long decr(long incr, long initialValue) {
		throw new NotSupportException("decr");
	}

	public Object get() {
		return value;
	}

	public void set(Object value) {
		this.value = value;
	}

	public long incr(long delta) {
		throw new NotSupportException("incr");
	}

	public long decr(long delta) {
		throw new NotSupportException("decr");
	}
}
