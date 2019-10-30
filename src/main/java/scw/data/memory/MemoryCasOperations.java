package scw.data.memory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.data.cas.CAS;
import scw.data.cas.CASOperations;
import scw.data.cas.SimpleCAS;

public class MemoryCasOperations implements CASOperations {
	private final MemoryCacheManager memoryCacheManager;

	public MemoryCasOperations(MemoryCacheManager memoryCacheManager) {
		this.memoryCacheManager = memoryCacheManager;
	}

	public boolean cas(String key, Object value, int exp, long cas) {
		MemoryCache memoryCache = memoryCacheManager.createDefaultMemoryCache(key);
		if (memoryCache.set(new SimpleCAS<Object>(cas, value))) {
			memoryCache.setExpire(exp);
			return true;
		}
		return false;
	}

	public <T> CAS<T> get(String key) {
		MemoryCache memoryCache = memoryCacheManager.getMemoryCache(key);
		if (memoryCache == null) {
			return null;
		}

		return memoryCache.get();
	}

	public void set(String key, Object value, int exp) {
		MemoryCache memoryCache = memoryCacheManager.createDefaultMemoryCache(key);
		memoryCache.set(value);
		memoryCache.setExpire(exp);
	}

	public boolean add(String key, Object value, int exp) {
		MemoryCache memoryCache = memoryCacheManager.createDefaultMemoryCache(key);
		if (memoryCache.setIfAbsent(value)) {
			memoryCache.setExpire(exp);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> Map<String, CAS<T>> get(Collection<String> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.EMPTY_MAP;
		}

		Map<String, CAS<T>> map = new LinkedHashMap<String, CAS<T>>(keys.size());
		for (String key : keys) {
			CAS<T> value = get(key);
			if (value == null) {
				continue;
			}

			map.put(key, value);
		}
		return map;
	}

	public boolean delete(String key, long cas) {
		return memoryCacheManager.delete(key, cas);
	}

	public boolean delete(String key) {
		return memoryCacheManager.delete(key);
	}

}
