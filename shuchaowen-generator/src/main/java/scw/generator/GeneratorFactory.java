package scw.generator;

public interface GeneratorFactory<K, V> {
	Generator<V> getGenerator(K key);
}
