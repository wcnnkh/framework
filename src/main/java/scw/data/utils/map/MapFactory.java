package scw.data.utils.map;

public interface MapFactory<K, V> {
	Map<K, V> getMap(String key);
}
