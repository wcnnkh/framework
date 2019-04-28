package scw.data.memcached;

import java.util.Collection;
import java.util.Map;

public interface Commands<K, V> {
	<T> T get(K key);
	
	<T> T getAndTouch(K key, int newExp);

	<T> CAS<T> gets(K key);

	boolean set(K key, V data);

	boolean set(K key, int exp, V data);

	boolean add(K key, V value);

	boolean add(K key, int exp, V data);

	boolean cas(K key, V data, long cas);

	boolean cas(K key, int exp, V data, long cas);

	boolean touch(String key, int exp);

	<T> Map<K, T> get(Collection<K> keyCollections);

	<T> Map<K, CAS<T>> gets(Collection<String> keyCollections);

	long incr(K key, long incr);

	long incr(K key, long incr, long initValue);

	long decr(K key, long decr);

	long decr(K key, long decr, long initValue);

	boolean delete(K key);

	boolean delete(K key, long cas, long opTimeout);
}
