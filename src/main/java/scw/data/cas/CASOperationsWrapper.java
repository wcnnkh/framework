package scw.data.cas;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;

public class CASOperationsWrapper implements CASOperations {
	private final CASOperations casOperations;
	private final String prefix;

	public CASOperationsWrapper(CASOperations casOperations, String prefix) {
		this.casOperations = casOperations;
		this.prefix = prefix;
	}

	private String formatKey(String key) {
		return StringUtils.isEmpty(prefix) ? key : (prefix + key);
	}

	public boolean cas(String key, Object value, int exp, long cas) {
		return casOperations.cas(formatKey(key), value, exp, cas);
	}

	public boolean delete(String key, long cas) {
		return casOperations.delete(formatKey(key), cas);
	}

	public <T> CAS<T> get(String key) {
		return casOperations.get(formatKey(key));
	}

	public void set(String key, Object value, int exp) {
		casOperations.set(formatKey(key), value, exp);
	}

	public boolean delete(String key) {
		return casOperations.delete(formatKey(key));
	}

	public boolean add(String key, Object value, int exp) {
		return casOperations.add(key, value, exp);
	}

	public <T> Map<String, CAS<T>> get(Collection<String> keys) {
		if (StringUtils.isEmpty(prefix)) {
			return casOperations.get(keys);
		} else {
			if (CollectionUtils.isEmpty(keys)) {
				return new HashMap<String, CAS<T>>(2);
			}

			Map<String, String> keyMap = new HashMap<String, String>(keys.size());
			for (String key : keys) {
				if (key == null) {
					continue;
				}

				keyMap.put(formatKey(key), key);
			}

			Map<String, CAS<T>> dataMap = casOperations.get(keyMap.keySet());
			if (CollectionUtils.isEmpty(dataMap)) {
				return new HashMap<String, CAS<T>>(2);
			}

			Map<String, CAS<T>> result = new HashMap<String, CAS<T>>(dataMap.size(), 1);
			for (Entry<String, CAS<T>> entry : dataMap.entrySet()) {
				String newKey = keyMap.get(entry.getKey());
				if (newKey == null) {
					continue;
				}

				result.put(newKey, entry.getValue());
			}
			return result;
		}
	}

}
