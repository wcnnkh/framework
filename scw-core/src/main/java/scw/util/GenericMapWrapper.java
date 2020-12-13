package scw.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GenericMapWrapper<K, V> implements GenericMap<K, V> {
	private GenericMap<K, V> targetMap;

	public GenericMapWrapper(GenericMap<K, V> targetMap) {
		this.targetMap = targetMap;
	}

	public int size() {
		return targetMap.size();
	}

	public boolean isEmpty() {
		return targetMap.isEmpty();
	}

	public boolean containsKey(Object key) {
		return targetMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return targetMap.containsValue(value);
	}

	public V get(Object key) {
		return targetMap.get(key);
	}

	public V put(K key, V value) {
		return targetMap.put(key, value);
	}

	public V remove(Object key) {
		return targetMap.remove(key);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		targetMap.putAll(m);
	}

	public void clear() {
		targetMap.clear();
	}

	public Set<K> keySet() {
		return targetMap.keySet();
	}

	public Collection<V> values() {
		return targetMap.values();
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return targetMap.entrySet();
	}

	public V putIfAbsent(K key, V value) {
		return targetMap.putIfAbsent(key, value);
	}
}
