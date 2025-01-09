package io.basc.framework.util.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMultiValueMap<K, V> implements MultiValueMap<K, V>, Serializable {
	private static final long serialVersionUID = 1L;

	protected abstract Map<K, List<V>> getTargetMap();

	protected List<V> createList() {
		return new ArrayList<V>(8);
	}

	@Override
	public void adds(K key, List<V> value) {
		List<V> values = getTargetMap().get(key);
		if (values == null) {
			values = createList();
			getTargetMap().put(key, values);
		}
		values.addAll(value);
	}

	public V getFirst(K key) {
		List<V> values = getTargetMap().get(key);
		return (values != null ? values.get(0) : null);
	}

	public void set(K key, V value) {
		List<V> values = createList();
		values.add(value);
		getTargetMap().put(key, values);
	}

	// Map implementation

	public int size() {
		return getTargetMap().size();
	}

	public boolean isEmpty() {
		return getTargetMap().isEmpty();
	}

	public boolean containsKey(Object key) {
		return getTargetMap().containsKey(key);
	}

	public boolean containsValue(Object value) {
		return getTargetMap().containsValue(value);
	}

	public List<V> get(Object key) {
		return getTargetMap().get(key);
	}

	public List<V> put(K key, List<V> value) {
		return getTargetMap().put(key, value);
	}

	public List<V> remove(Object key) {
		return getTargetMap().remove(key);
	}

	public void putAll(Map<? extends K, ? extends List<V>> m) {
		getTargetMap().putAll(m);
	}

	public void clear() {
		getTargetMap().clear();
	}

	public Set<K> keySet() {
		return getTargetMap().keySet();
	}

	public Collection<List<V>> values() {
		return getTargetMap().values();
	}

	public Set<java.util.Map.Entry<K, List<V>>> entrySet() {
		return getTargetMap().entrySet();
	}

	@Override
	public boolean equals(Object obj) {
		return getTargetMap().equals(obj);
	}

	@Override
	public int hashCode() {
		return getTargetMap().hashCode();
	}

	@Override
	public String toString() {
		return getTargetMap().toString();
	}

}
