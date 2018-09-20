package shuchaowen.core.db.cache;

public class NotCacheFactory implements CacheFactory{
	public Cache getCache(Class<?> tableClass) {
		return null;
	}
}
