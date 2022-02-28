package io.basc.framework.data.memory;

import java.util.Map;

import io.basc.framework.data.KeyValueOperations;
import io.basc.framework.io.SerializerUtils;

public abstract class AbstractMapOperations<K, V> implements KeyValueOperations<K, V> {
	public abstract Map<K, V> getMap();

	protected abstract Map<K, V> createMap();

	protected V cloneValue(V value) {
		return SerializerUtils.clone(value);
	}

	@Override
	public V get(K key) {
		Map<K, V> map = getMap();
		if (map == null) {
			return null;
		}

		return map.get(key);
	}

	@Override
	public boolean setIfAbsent(K key, V value) {
		Map<K, V> map = getMap();
		if (map == null) {
			map = createMap();
		}

		if (map == null) {
			return false;
		}

		return map.putIfAbsent(key, cloneValue(value)) == null;
	}

	@Override
	public void set(K key, V value) {
		Map<K, V> map = getMap();
		if (map == null) {
			map = createMap();
		}

		if (map == null) {
			return;
		}

		map.put(key, cloneValue(value));
	}

	@Override
	public boolean setIfPresent(K key, V value) {
		Map<K, V> map = getMap();
		if (map == null) {
			map = createMap();
		}

		if (map == null) {
			return false;
		}

		return map.computeIfPresent(key, (k, v) -> v) != null;
	}

	@Override
	public boolean delete(K key) {
		Map<K, V> map = getMap();
		if (map == null) {
			return false;
		}

		return map.remove(key) != null;
	}

	@Override
	public boolean exists(K key) {
		Map<K, V> map = getMap();
		if (map == null) {
			return false;
		}

		return map.containsKey(key);
	}
}
