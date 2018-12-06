package shuchaowen.core.db.storage.cache;

import shuchaowen.common.utils.XTime;

public class CacheConfig {
	public static final int DATA_DEFAULT_EXP_TIME = 7 * ((int) XTime.ONE_DAY / 1000);
	public static final CacheConfig NOT_CACHE = new CacheConfig(CacheType.no, 0, false);
	public static final CacheConfig DEFAULT_CACHE_CONFIG = new CacheConfig(CacheType.lazy, DATA_DEFAULT_EXP_TIME,
			false);
	
	private final CacheType cacheType;
	private final int exp;
	private final boolean async;
	
	public CacheConfig(CacheType cacheType, int exp, boolean async) {
		this.cacheType = cacheType;
		this.exp = cacheType == CacheType.full ? 0 : exp;
		this.async = async;
	}

	public CacheType getCacheType() {
		return cacheType;
	}

	public int getExp() {
		return exp;
	}

	public boolean isAsync() {
		return async;
	}
}
