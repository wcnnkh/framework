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
		String key = getCountLimitConfig().getName();
		int exp = (int) getCountLimitConfig().getPeriod(TimeUnit.SECONDS);
		memcached.add(key, exp, (System.currentTimeMillis()/1000) + exp);
		memcached.add(getCountLimitConfig().getName(), (int) getCountLimitConfig().getPeriod(TimeUnit.SECONDS), 0);
		return memcached.incr(getCountLimitConfig().getName(), 1) <= getCountLimitConfig().getMaxCount();
	}

	public long getCount() {
		Number value = memcached.get(getCountLimitConfig().getName());
		return value == null ? 0 : value.longValue();
	}

	public void reset() {
		memcached.set(getCountLimitConfig().getName(), (int) getCountLimitConfig().getPeriod(TimeUnit.SECONDS), 0);
	}
}
