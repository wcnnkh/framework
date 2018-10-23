package shuchaowen.core.db.storage.cache;

public class CacheConfig {
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
