package scw.core;

public interface KeyValuePairFilter<K, V> {
	KeyValuePair<K, V> filter(KeyValuePair<K, V> pair);
}
