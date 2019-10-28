package scw.security.limit;

import java.util.concurrent.TimeUnit;

import scw.data.cache.CacheService;

public final class DefaultCountLimit extends AbstractCountLimit {
	private final CacheService cacheService;

	public DefaultCountLimit(CountLimitConfig countLimitConfig, CacheService cacheService) {
		super(countLimitConfig);
		this.cacheService = cacheService;
	}

	public boolean incr() {
		return cacheService.incr(getCountLimitConfig().getName(), 1, 1,
				(int) getCountLimitConfig().getPeriod(TimeUnit.SECONDS)) <= getCountLimitConfig().getMaxCount();
	}

	public long getCount() {
		return cacheService.get(getCountLimitConfig().getName());
	}
}
