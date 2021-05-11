package scw.event.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import scw.core.utils.CollectionUtils;
import scw.event.EventType;
import scw.event.PairChangeEvent;
import scw.event.NamedEventDispatcher;
import scw.util.SmartMap;

public class ObservableMap<K, V> extends SmartMap<K, V> {
	private static final long serialVersionUID = 1L;
	private final NamedEventDispatcher<K, PairChangeEvent<K, V>> eventDispatcher;

	public ObservableMap(boolean concurrent) {
		this(concurrent, new DefaultNamedEventDispatcher<K, PairChangeEvent<K, V>>(concurrent));
	}

	public ObservableMap(boolean concurrent, NamedEventDispatcher<K, PairChangeEvent<K, V>> eventDispatcher) {
		super(concurrent);
		this.eventDispatcher = eventDispatcher;
	}

	public NamedEventDispatcher<K, PairChangeEvent<K, V>> getEventDispatcher() {
		return eventDispatcher;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return Collections.unmodifiableSet(super.entrySet());
	}

	@Override
	public Set<K> keySet() {
		return Collections.unmodifiableSet(super.keySet());
	}

	@Override
	public Collection<V> values() {
		return Collections.unmodifiableCollection(super.values());
	}

	@Override
	public V put(K key, V value) {
		V v;
		if (isConcurrent()) {
			v = super.put(key, value);
		} else {
			synchronized (this) {
				v = super.put(key, value);
			}
		}

		PairChangeEvent<K, V> event = null;
		if (v == null) {
			event = new PairChangeEvent<K, V>(EventType.CREATE, key, value);
		} else {
			if (!v.equals(value)) {
				event = new PairChangeEvent<K, V>(EventType.UPDATE, key, value);
			}
		}

		if (event != null) {
			getEventDispatcher().publishEvent(key, event);
		}
		return v;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		K keyToUse = (K)key;
		V v;
		if (isConcurrent()) {
			v = super.remove(keyToUse);
		} else {
			synchronized (this) {
				v = super.remove(keyToUse);
			}
		}

		if (v != null) {
			eventDispatcher.publishEvent(keyToUse, new PairChangeEvent<K, V>(EventType.DELETE, keyToUse, v));
		}
		return v;
	}

	@Override
	public void clear() {
		Map<K, V> cloneMap;
		if (isConcurrent()) {
			cloneMap = new HashMap<K, V>(this);
			super.clear();
		} else {
			synchronized (this) {
				cloneMap = new HashMap<K, V>(this);
				super.clear();
			}
		}

		for (Entry<K, V> entry : cloneMap.entrySet()) {
			getEventDispatcher().publishEvent(entry.getKey(), new PairChangeEvent<K, V>(EventType.DELETE, entry.getKey(), entry.getValue()));
		}
	}

	@Override
	public V putIfAbsent(K key, V value) {
		V v;
		if (isConcurrent()) {
			v = super.putIfAbsent(key, value);
		} else {
			synchronized (this) {
				v = super.putIfAbsent(key, value);
			}
		}

		if (v != null) {
			getEventDispatcher().publishEvent(key, new PairChangeEvent<K, V>(EventType.CREATE, key, value));
		}
		return v;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if (CollectionUtils.isEmpty(m)) {
			return;
		}

		for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
}
