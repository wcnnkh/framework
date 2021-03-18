package scw.data;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import scw.context.Destroy;
import scw.context.support.LifecycleAuxiliary;

public abstract class AbstractExpiredStorageFactory implements ExpiredStorageFactory, Destroy {
	private ConcurrentHashMap<Integer, ExpiredStorage> cacheMap = new ConcurrentHashMap<Integer, ExpiredStorage>();

	protected abstract ExpiredStorage createExpiredCache(int exp);

	public ExpiredStorage getExpiredCache(int exp) {
		ExpiredStorage create = createExpiredCache(exp);
		ExpiredStorage cache = cacheMap.putIfAbsent(exp, create);
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
		for (Entry<Integer, ExpiredStorage> entry : cacheMap.entrySet()) {
			try {
				LifecycleAuxiliary.destroy(entry.getValue());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
