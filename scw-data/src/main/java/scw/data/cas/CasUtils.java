package scw.data.cas;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class CasUtils {
	private CasUtils() {
	};

	@SuppressWarnings("unchecked")
	private static <K, V> boolean casMapPut(CASOperations casOperations, String key, K field, V value,
			Supplier<Map<K, V>> createCallable) {
		CAS<Object> cas = casOperations.get(key);
		if (cas == null) {
			Map<K, V> valueMap = createCallable.get();
			valueMap.put(field, value);
			return casOperations.cas(key, valueMap, 0, 0);
		} else {
			Map<K, V> valueMap = (Map<K, V>) cas.getValue();
			valueMap.put(field, value);
			return casOperations.cas(key, valueMap, 0, cas.getCas());
		}
	}

	@SuppressWarnings("unchecked")
	private static <K, V> boolean casMapRemove(CASOperations casOperations, String key, K field,
			Supplier<Map<K, V>> createCallable) {
		CAS<Object> cas = casOperations.get(key);
		if (cas == null) {
			Map<K, V> valueMap = createCallable.get();
			return casOperations.cas(key, valueMap, 0, 0);
		} else {
			Map<K, V> valueMap = (Map<K, V>) cas.getValue();
			valueMap.remove(field);
			return casOperations.cas(key, valueMap, 0, cas.getCas());
		}
	}

	public static <K, V> void mapRemove(CASOperations casOperations, String key, K field,
			Supplier<Map<K, V>> createCallable) {
		while (casMapRemove(casOperations, key, field, createCallable)) {
			break;
		}
	}

	public static <K, V> void mapPut(CASOperations casOperations, String key, K field, V value,
			Supplier<Map<K, V>> createCallable) {
		while (casMapPut(casOperations, key, field, value, createCallable)) {
			break;
		}
	}

	public static <K, V> void mapRemove(CASOperations casOperations, String key, K field) {
		mapRemove(casOperations, key, field, new CreateLinkedHashMapSupplier<K, V>());
	}

	public static <K, V> void mapPut(CASOperations casOperations, String key, K field, V value) {
		mapPut(casOperations, key, field, value, new CreateLinkedHashMapSupplier<K, V>());
	}

	private static class CreateLinkedHashMapSupplier<K, V> implements Supplier<Map<K, V>> {

		public LinkedHashMap<K, V> get() {
			return new LinkedHashMap<K, V>();
		}
	}
}
