package scw.db.cache;

import scw.core.Constants;
import scw.data.Cache;

public final class DefaultCacheManager extends AbstractCacheManager {
	private Cache cache;
	private String keyPrefix;

	public DefaultCacheManager(Cache cache, String keyPrefix) {
		this.cache = cache;
		this.keyPrefix = keyPrefix;
	}
	
	public DefaultCacheManager(Cache cache) {
		this(cache, Constants.DEFAULT_PREFIX);
	}

	@Override
	public Cache getCache() {
		return cache;
	}

	@Override
	public String formatKey(String key) {
		return keyPrefix == null ? key : (keyPrefix + key);
	}
}
