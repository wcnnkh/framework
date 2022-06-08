package io.basc.framework.util;

import io.basc.framework.lang.NamedThreadLocal;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ThreadLocalMap<K, V> implements Map<K, V> {
	private final ThreadLocal<Map<K, V>> local;

	public ThreadLocalMap() {
		this(new ThreadLocal<>());
	}

	public ThreadLocalMap(String name) {
		this(new NamedThreadLocal<>(name));
	}

	public ThreadLocalMap(ThreadLocal<Map<K, V>> local) {
		Assert.requiredArgument(local != null, "local");
		this.local = local;
	}

	public ThreadLocal<Map<K, V>> getLocal() {
		return local;
	}

	@Override
	public int size() {
		Map<K, V> map = local.get();
		return map == null ? 0 : map.size();
	}

	@Override
	public boolean isEmpty() {
		Map<K, V> map = local.get();
		return map == null ? true : map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		Map<K, V> map = local.get();
		return map == null ? false : map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		Map<K, V> map = local.get();
		return map == null ? false : map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		Map<K, V> map = local.get();
		return map == null ? null : map.get(key);
	}

	@Override
	public V put(K key, V value) {
		Map<K, V> map = local.get();
		if (map == null) {
			map = new LinkedHashMap<>();
			local.set(map);
		}
		return map.put(key, value);
	}

	@Override
	public V remove(Object key) {
		Map<K, V> map = local.get();
		return map == null ? null : map.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		Map<K, V> map = local.get();
		if (map == null) {
			map = new LinkedHashMap<>();
			local.set(map);
		}
		map.putAll(map);
	}

	@Override
	public void clear() {
		local.remove();
	}

	@Override
	public Set<K> keySet() {
		Map<K, V> map = local.get();
		return map == null ? Collections.emptySet() : map.keySet();
	}

	@Override
	public Collection<V> values() {
		Map<K, V> map = local.get();
		return map == null ? Collections.emptyList() : map.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		Map<K, V> map = local.get();
		return map == null ? Collections.emptySet() : map.entrySet();
	}
}
