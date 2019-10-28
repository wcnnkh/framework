package scw.data.cache;

import java.util.Collection;
import java.util.Map;

import scw.data.memcached.Memcached;

public class CacheServiceWrapper implements CacheService {
	private final CacheService cacheService;

	public CacheServiceWrapper(Memcached cacheService) {
		this.cacheService = cacheService;
	}

	public <T> T get(String key) {
		return cacheService.get(key);
	}

	public <T> T getAndTouch(String key, int newExp) {
		return cacheService.getAndTouch(key, newExp);
	}

	public boolean set(String key, Object value) {
		return cacheService.set(key, value);
	}

	public boolean set(String key, int exp, Object value) {
		return cacheService.set(key, exp, value);
	}

	public boolean add(String key, Object value) {
		return cacheService.add(key, value);
	}

	public boolean add(String key, int exp, Object value) {
		return cacheService.add(key, exp, value);
	}

	public boolean touch(String key, int exp) {
		return cacheService.touch(key, exp);
	}

	public <T> Map<String, T> get(Collection<String> keyCollections) {
		return cacheService.get(keyCollections);
	}

	public boolean delete(String key) {
		return cacheService.delete(key);
	}

	public boolean isExist(String key) {
		return cacheService.isExist(key);
	}

	public long incr(String key, long delta) {
		return cacheService.incr(key, delta);
	}

	public long incr(String key, long delta, long initialValue) {
		return cacheService.incr(key, delta, initialValue);
	}

	public long incr(String key, long delta, long initialValue, int exp) {
		return cacheService.incr(key, delta, initialValue, exp);
	}

	public long decr(String key, long delta) {
		return cacheService.decr(key, delta);
	}

	public long decr(String key, long delta, long initialValue) {
		return cacheService.decr(key, delta, initialValue);
	}

	public long decr(String key, long delta, long initialValue, int exp) {
		return cacheService.decr(key, delta, initialValue, exp);
	}
}
