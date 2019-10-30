package scw.data.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.data.cache.CacheService;
import scw.data.cas.CAS;

@SuppressWarnings("unchecked")
public final class MemoryCacheService implements CacheService {
	private final MemoryCacheManager memoryCacheManager;

	public MemoryCacheService(MemoryCacheManager memoryCacheManager) {
		this.memoryCacheManager = memoryCacheManager;
	}

	public <T> T get(String key) {
		MemoryCache memoryCache = memoryCacheManager.getMemoryCache(key);
		if (memoryCache == null) {
			return null;
		}

		CAS<T> cas = memoryCache.get();
		return cas == null ? null : cas.getValue();
	}

	public <T> T getAndTouch(String key, int newExp) {
		MemoryCache memoryCache = memoryCacheManager.getMemoryCache(key);
		if (memoryCache == null) {
			return null;
		}

		memoryCache.setExpire(newExp);
		memoryCache.touch();
		CAS<T> cas = memoryCache.get();
		return cas == null ? null : cas.getValue();
	}

	public boolean set(String key, Object value) {
		MemoryCache memoryCache = memoryCacheManager.createDefaultMemoryCache(key);
		memoryCache.set(value);
		return true;
	}

	public boolean set(String key, int exp, Object value) {
		MemoryCache memoryCache = memoryCacheManager.createDefaultMemoryCache(key);
		memoryCache.set(value);
		memoryCache.setExpire(exp);
		return true;
	}

	public boolean add(String key, Object value) {
		MemoryCache memoryCache = memoryCacheManager.createDefaultMemoryCache(key);
		return memoryCache.setIfAbsent(value);
	}

	public boolean add(String key, int exp, Object value) {
		MemoryCache memoryCache = memoryCacheManager.createDefaultMemoryCache(key);
		if (!memoryCache.setIfAbsent(value)) {
			return false;
		}

		memoryCache.setExpire(exp);
		return true;
	}

	public boolean touch(String key, int exp) {
		MemoryCache memoryCache = memoryCacheManager.getMemoryCache(key);
		if (memoryCache == null) {
			return false;
		}

		memoryCache.setExpire(exp);
		return true;
	}

	public <T> Map<String, T> get(Collection<String> keyCollections) {
		if (CollectionUtils.isEmpty(keyCollections)) {
			return Collections.EMPTY_MAP;
		}

		Map<String, T> map = new HashMap<String, T>(keyCollections.size());
		for (String key : keyCollections) {
			T value = get(key);
			if (value == null) {
				continue;
			}

			map.put(key, value);
		}
		return map;
	}

	public boolean isExist(String key) {
		return memoryCacheManager.getMemoryCache(key) != null;
	}

	public long incr(String key, long delta) {
		return incr(key, delta, 0, 0);
	}

	public long incr(String key, long delta, long initialValue) {
		return incr(key, delta, initialValue, 0);
	}

	public long incr(String key, long delta, long initialValue, int exp) {
		MemoryCache memoryCache = memoryCacheManager.createCounterMemoryCache(key);
		long v = memoryCache.incr(delta, initialValue);
		memoryCache.setExpire(exp);
		return v;
	}

	public long decr(String key, long delta) {
		return decr(key, delta, 0, 0);
	}

	public long decr(String key, long delta, long initialValue) {
		return decr(key, delta, initialValue, 0);
	}

	public long decr(String key, long delta, long initialValue, int exp) {
		MemoryCache memoryCache = memoryCacheManager.createCounterMemoryCache(key);
		long v = memoryCache.decr(-delta, initialValue);
		memoryCache.setExpire(exp);
		return v;
	}

	public boolean delete(String key) {
		return memoryCacheManager.delete(key);
	}
}
