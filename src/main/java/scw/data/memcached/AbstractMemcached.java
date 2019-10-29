package scw.data.memcached;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.druid.util.StringUtils;

import scw.core.utils.CollectionUtils;
import scw.data.cas.CASOperations;
import scw.data.cas.CASOperationsWrapper;

public abstract class AbstractMemcached implements Memcached {

	public abstract Memcached getTargetMemcached();

	public abstract String getKeyPrefix();

	private String formatKey(String key) {
		String prefix = getKeyPrefix();
		return prefix == null ? key : (prefix + key);
	}

	public <T> T get(String key) {
		return getTargetMemcached().get(formatKey(key));
	}

	public <T> T getAndTouch(String key, int newExp) {
		return getTargetMemcached().getAndTouch(formatKey(key), newExp);
	}

	public boolean set(String key, Object data) {
		return getTargetMemcached().set(formatKey(key), data);
	}

	public boolean set(String key, int exp, Object data) {
		return getTargetMemcached().set(formatKey(key), exp, data);
	}

	public boolean add(String key, Object value) {
		return getTargetMemcached().add(formatKey(key), value);
	}

	public boolean add(String key, int exp, Object data) {
		return getTargetMemcached().add(formatKey(key), data);
	}

	public boolean touch(String key, int exp) {
		return getTargetMemcached().touch(formatKey(key), exp);
	}

	public <T> Map<String, T> get(Collection<String> keyCollections) {
		if (StringUtils.isEmpty(getKeyPrefix())) {
			return getTargetMemcached().get(keyCollections);
		}

		if (CollectionUtils.isEmpty(keyCollections)) {
			return new LinkedHashMap<String, T>(2);
		}

		Map<String, String> keyMap = new HashMap<String, String>(keyCollections.size());
		for (String key : keyCollections) {
			if (key == null) {
				continue;
			}

			keyMap.put(formatKey(key), key);
		}

		Map<String, T> dataMap = getTargetMemcached().get(keyMap.keySet());
		if (CollectionUtils.isEmpty(dataMap)) {
			return new LinkedHashMap<String, T>(2);
		}

		Map<String, T> result = new LinkedHashMap<String, T>(dataMap.size(), 1);
		for (Entry<String, T> entry : dataMap.entrySet()) {
			String newKey = keyMap.get(entry.getKey());
			if (newKey == null) {
				continue;
			}

			result.put(newKey, entry.getValue());
		}
		return result;
	}

	public long incr(String key, long delta) {
		return getTargetMemcached().incr(formatKey(key), delta);
	}

	public long incr(String key, long delta, long initValue) {
		return getTargetMemcached().incr(formatKey(key), delta, initValue);
	}

	public long decr(String key, long delta) {
		return getTargetMemcached().decr(formatKey(key), delta);
	}

	public long decr(String key, long delta, long initValue) {
		return getTargetMemcached().decr(formatKey(key), delta, initValue);
	}

	public boolean delete(String key) {
		return getTargetMemcached().delete(formatKey(key));
	}

	public boolean isExist(String key) {
		return getTargetMemcached().delete(formatKey(key));
	}

	/**
	 * 默认的实现每次都会重新创建一个对象
	 */
	public CASOperations getCASOperations() {
		return new CASOperationsWrapper(getTargetMemcached().getCASOperations(), getKeyPrefix());
	}

	public long incr(String key, long incr, long initValue, int exp) {
		return getTargetMemcached().incr(key, incr, initValue, exp);
	}

	public long decr(String key, long decr, long initValue, int exp) {
		return getTargetMemcached().decr(key, decr, initValue, exp);
	}
}
