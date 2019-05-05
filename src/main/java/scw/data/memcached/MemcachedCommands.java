package scw.data.memcached;

import java.util.Collection;
import java.util.Map;

public interface MemcachedCommands<K, V> {
	V get(K key);
	
	V getAndTouch(K key, int newExp);

	CAS<V> gets(K key);

	boolean set(K key, V data);

	boolean set(K key, int exp, V data);

	boolean add(K key, V value);

	boolean add(K key, int exp, V data);

	boolean cas(K key, V data, long cas);

	boolean cas(K key, int exp, V data, long cas);

	boolean touch(String key, int exp);

	Map<K, V> get(Collection<K> keyCollections);

	Map<K, CAS<V>> gets(Collection<K> keyCollections);

	long incr(K key, long incr);

	long incr(K key, long incr, long initValue);

	long decr(K key, long decr);

	long decr(K key, long decr, long initValue);

	boolean delete(K key);

	boolean delete(K key, long cas, long opTimeout);
}
