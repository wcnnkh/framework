package scw.core.lazy;

import java.util.Map;

public interface MapLazyFactory<K, V> extends LazyFactory<K, V>, Map<K, V> {
	Map<K, V> createMap();
}
