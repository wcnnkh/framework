package scw.db.cache;

import java.util.Collection;
import java.util.Map;

import scw.data.memcached.Memcached;

public class MemcachedLazyCacheManager extends LazyDataManager {
	private final Memcached memcached;

	public MemcachedLazyCacheManager(Memcached memcached) {
		this.memcached = memcached;
	}

	@Override
	protected void set(String key, int exp, Object value) {
		memcached.set(key, exp, value);
	}

	@Override
	protected void del(String key) {
		memcached.delete(key);
	}

	@Override
	protected <T> T getAndTouch(Class<T> type, String key, int exp) {
		return memcached.getAndTouch(key, exp);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> Map<String, T> mget(Class<T> type, Collection<String> keys) {
		return (Map<String, T>) memcached.gets(keys);
	}

	@Override
	protected void addKey(String key) {
		memcached.set(key, "");
	}

	@Override
	protected boolean isExist(String key) {
		return memcached.isExist(key);
	}

}
