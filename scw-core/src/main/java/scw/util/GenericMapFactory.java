package scw.util;



public interface GenericMapFactory {
	<K, V> GenericMap<K, V> wrapper(java.util.Map<K, V> map);
}
