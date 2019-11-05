package scw.data;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import scw.core.Destroy;
import scw.core.utils.XUtils;

public abstract class AbstractExpireCacheFactory implements ExpireCacheFactory, Destroy{
	private ConcurrentHashMap<Integer, Cache> cacheMap = new ConcurrentHashMap<Integer, Cache>();

	protected abstract Cache createCache(int exp);

	public Cache getCache(int exp) {
		Cache create = createCache(exp);
		Cache cache = cacheMap.putIfAbsent(exp, create);
		if (cache == null) {
			XUtils.init(create);
			cache = create;
		}
		return cache;
	}

	public void destroy() {
		for (Entry<Integer, Cache> entry : cacheMap.entrySet()) {
			XUtils.destroy(entry.getValue());
		}
	}
}
