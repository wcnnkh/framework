package scw.value;


public interface ConfigurableValueFactory<K> extends ValueFactory<K>{
	boolean put(K key, Value value);
	
	boolean putIfAbsent(K key, Value value);

	boolean remove(K key);
}
