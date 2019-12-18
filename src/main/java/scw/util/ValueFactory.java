package scw.util;

public interface ValueFactory<K, V extends Value> {
	V get(K key);
}
