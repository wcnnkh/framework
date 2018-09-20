package shuchaowen.core.db.cache;

public interface CacheFactory {
	Cache getCache(Class<?> tableClass);
}
