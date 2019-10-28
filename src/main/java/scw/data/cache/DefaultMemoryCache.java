package scw.data.cache;

import scw.core.exception.NotSupportException;

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
