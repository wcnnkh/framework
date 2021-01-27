package scw.util;

import java.util.Map;

public interface GenericMap<K, V> extends Map<K, V>, Cloneable{
	V putIfAbsent(K key, V value);
	
	GenericMap<K, V> clone();
}
