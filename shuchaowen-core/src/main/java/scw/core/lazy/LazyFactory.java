package scw.core.lazy;

public interface LazyFactory<K, V> {
	V get(K key);

	V createValue(K key);
}
