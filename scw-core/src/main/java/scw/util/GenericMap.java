package scw.util;

import java.util.Map;

public interface GenericMap<K, V> extends Map<K, V> {
	V putIfAbsent(K key, V value);
}
