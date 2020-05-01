package scw.util.value;

public interface BaseValueFactory<K> {
	Value get(K key);
}
