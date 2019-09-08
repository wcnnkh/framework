package scw.data.memcached;

import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.Callable;
import scw.data.cas.CAS;

public final class MemcachedUtils {
	private MemcachedUtils() {
	};

	@SuppressWarnings("unchecked")
	private static <K, V> boolean casMapPut(Memcached memcached, String key,
			K field, V value, Callable<Map<K, V>> createCallable) {
		CAS<Object> cas = memcached.getCASOperations().get(key);
		if (cas == null) {
			Map<K, V> valueMap = createCallable.call();
			valueMap.put(field, value);
			return memcached.getCASOperations().cas(key, valueMap, 0, 0);
		} else {
			Map<K, V> valueMap = (Map<K, V>) cas.getValue();
			valueMap.put(field, value);
			return memcached.getCASOperations().cas(key, valueMap, 0,
					cas.getCas());
		}
	}

	@SuppressWarnings("unchecked")
	private static <K, V> boolean casMapRemove(Memcached memcached, String key,
			K field, Callable<Map<K, V>> createCallable) {
		CAS<Object> cas = memcached.getCASOperations().get(key);
		if (cas == null) {
			Map<K, V> valueMap = createCallable.call();
			return memcached.getCASOperations().cas(key, valueMap, 0, 0);
		} else {
			Map<K, V> valueMap = (Map<K, V>) cas.getValue();
			valueMap.remove(field);
			return memcached.getCASOperations().cas(key, valueMap, 0,
					cas.getCas());
		}
	}

	public static <K, V> void mapRemove(Memcached memcached, String key,
			K field, Callable<Map<K, V>> createCallable) {
		while (casMapRemove(memcached, key, field, createCallable)) {
			break;
		}
	}

	public static <K, V> void mapPut(Memcached memcached, String key, K field,
			V value, Callable<Map<K, V>> createCallable) {
		while (casMapPut(memcached, key, field, value, createCallable)) {
			break;
		}
	}

	public static <K, V> void mapRemove(Memcached memcached, String key, K field) {
		mapRemove(memcached, key, field,
				new CreateLinkedHashMapCallable<K, V>());
	}

	public static <K, V> void mapPut(Memcached memcached, String key, K field,
			V value) {
		mapPut(memcached, key, field, value,
				new CreateLinkedHashMapCallable<K, V>());
	}

	private static class CreateLinkedHashMapCallable<K, V> implements
			Callable<Map<K, V>> {

		public LinkedHashMap<K, V> call() {
			return new LinkedHashMap<K, V>();
		}
	}
}
