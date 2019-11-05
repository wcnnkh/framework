package scw.data;

public interface CacheFactory<T extends Cache> {
	Cache getCache(String key, int exp);
}
