package scw.compatible.map;


public interface MapCompatible {
	<K, V> CompatibleMap<K, V> wrapper(java.util.Map<K, V> map);
}
