package scw.db.cache;

import java.util.Collection;
import java.util.Map;

import scw.data.memcached.Memcached;

public class MemcachedLazyCacheManager extends LazyDataManager {
	private final Memcached memcached;

	public MemcachedLazyCacheManager(Memcached memcached, int exp, boolean key) {
		super(exp, key);
		this.memcached = memcached;
	}

	@Override
	protected void set(String key, Object value) {
		memcached.set(key, getExp(), value);
	}

	@Override
	protected void del(String key) {
		memcached.delete(key);
	}

	@Override
	protected <T> T get(Class<T> type, String key) {
		return memcached.get(key);
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
