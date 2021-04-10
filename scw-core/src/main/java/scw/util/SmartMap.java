package scw.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import scw.core.utils.ObjectUtils;

public class SmartMap<K, V> implements Map<K, V>, Cloneable {
	private final Map<K, V> targetMap;

	public SmartMap(boolean concurrent) {
		this(concurrent ? new ConcurrentHashMap<K, V>() : new HashMap<K, V>());
	}

	public SmartMap(boolean concurrent, int initialCapacity) {
		this(concurrent ? new ConcurrentHashMap<K, V>(initialCapacity) : new HashMap<K, V>(initialCapacity));
	}

	public SmartMap(Map<K, V> targetMap) {
		this.targetMap = targetMap;
	}

	public Map<K, V> getTargetMap() {
		return targetMap;
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
		/*
		 * if (targetMap instanceof ConcurrentMap) { return targetMap.putIfAbsent(key,
		 * value); }
		 * 
		 * //兼容1.5的写法 V v = get(key); if (v == null) { v = put(key, value); } return v;
		 */
	}

	public boolean isConcurrent() {
		return targetMap instanceof ConcurrentMap;
	}

	@Override
	public String toString() {
		return targetMap.toString();
	}

	@Override
	public int hashCode() {
		return targetMap.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return ObjectUtils.nullSafeEquals(targetMap, obj);
	}

	@Override
	public SmartMap<K, V> clone() {
		return new SmartMap<K, V>(CollectionFactory.clone(targetMap));
	}
}
