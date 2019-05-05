package scw.data.utils;

import java.util.Map;

import scw.data.memcached.Memcached;

/**
 * TODO 用于存储大量数据
 * 
 * @author asus1
 *
 * @param <V>
 */
public class MemcachedBigMap<V> implements scw.data.utils.Map<String, V> {
	private final Memcached memcached;
	private final String keyPrefix;
	private final String sizeKey;

	public MemcachedBigMap(Memcached memcached, String keyPrefix) {
		this.memcached = memcached;
		this.keyPrefix = keyPrefix;
		this.sizeKey = keyPrefix + "#size";
	}

	public int size() {
		return (int) longSize();
	}

	public long longSize() {
		Long size = (Long) memcached.get(sizeKey);
		return size == null ? 0 : size;
	}

	public boolean isEmpty() {
		return longSize() == 0;
	}

	@SuppressWarnings("unchecked")
	public V get(String key) {
		return (V) memcached.get(keyPrefix + key);
	}

	public V remove(String key) {
		V v = get(key);
		memcached.delete(keyPrefix + key);
		return v;
	}

	public boolean containsKey(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	public V put(String key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	public V putIfAbsent(String key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	public void putAll(Map<? extends String, ? extends V> m) {
		// TODO Auto-generated method stub

	}

	public Map<String, V> asLocalMap() {
		// TODO Auto-generated method stub
		return null;
	}

}
