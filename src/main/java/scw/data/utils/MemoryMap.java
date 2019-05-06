package scw.data.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryMap<K, V> extends ConcurrentHashMap<K, V> implements scw.data.utils.Map<K, V> {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public Map<K, V> asMap() {
		try {
			return (Map<K, V>) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
