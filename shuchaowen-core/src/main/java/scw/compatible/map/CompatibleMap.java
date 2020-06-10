package scw.compatible.map;


public interface CompatibleMap<K, V> extends java.util.Map<K, V> {
	V putIfAbsent(K key, V value);
	
	java.util.Map<K, V> getSourceMap();
}
