package scw.data;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Destroy;
import scw.core.utils.XUtils;

public abstract class AbstractExpireCacheFactory implements ExpiredCacheFactory, Destroy {
	private ConcurrentHashMap<Integer, ExpiredCache> cacheMap = new ConcurrentHashMap<Integer, ExpiredCache>();

	protected abstract ExpiredCache createExpiredCache(int exp);

	public ExpiredCache getExpiredCache(int exp) {
		ExpiredCache create = createExpiredCache(exp);
		ExpiredCache cache = cacheMap.putIfAbsent(exp, create);
		if (cache == null) {
			XUtils.init(create);
			XUtils.start(create);
			cache = create;
		}
		return cache;
	}

	public void destroy() {
		for (Entry<Integer, ExpiredCache> entry : cacheMap.entrySet()) {
			XUtils.destroy(entry.getValue());
		}
	}
}
