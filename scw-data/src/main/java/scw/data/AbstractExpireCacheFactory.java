package scw.data;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import scw.context.Destroy;
import scw.context.support.LifecycleAuxiliary;

public abstract class AbstractExpireCacheFactory implements ExpiredCacheFactory, Destroy {
	private ConcurrentHashMap<Integer, ExpiredCache> cacheMap = new ConcurrentHashMap<Integer, ExpiredCache>();

	protected abstract ExpiredCache createExpiredCache(int exp);

	public ExpiredCache getExpiredCache(int exp) {
		ExpiredCache create = createExpiredCache(exp);
		ExpiredCache cache = cacheMap.putIfAbsent(exp, create);
		if (cache == null) {
			try {
				LifecycleAuxiliary.init(create);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			cache = create;
		}
		return cache;
	}

	public void destroy() {
		for (Entry<Integer, ExpiredCache> entry : cacheMap.entrySet()) {
			try {
				LifecycleAuxiliary.destroy(entry.getValue());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
