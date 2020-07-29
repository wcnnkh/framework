package scw.compatible.map;

import java.util.Map;

public interface CompatibleMap<K, V> extends Map<K, V> {
	V putIfAbsent(K key, V value);
}
