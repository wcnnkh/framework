package scw.security.limit;

import java.util.concurrent.TimeUnit;

import scw.data.memcached.Memcached;

public class MemcachedCountLimit extends AbstractCountLimit {
	private final Memcached memcached;

	public MemcachedCountLimit(CountLimitConfig countLimitConfig, Memcached memcached) {
		super(countLimitConfig);
		this.memcached = memcached;
	}

	public boolean incr() {
		memcached.add(getCountLimitConfig().getName(), (int) getCountLimitConfig().getPeriod(TimeUnit.SECONDS), 0);
		return memcached.incr(getCountLimitConfig().getName(), 1) <= getCountLimitConfig().getMaxCount();
	}

	public long getCount() {
		Long value = memcached.get(getCountLimitConfig().getName());
		return value == null ? 0 : value;
	}

	public void reset() {
		memcached.set(getCountLimitConfig().getName(), (int) getCountLimitConfig().getPeriod(TimeUnit.SECONDS), 0);
	}
}
