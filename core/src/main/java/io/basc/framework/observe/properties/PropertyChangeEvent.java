package io.basc.framework.observe.properties;

import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;

public class PropertyChangeEvent<K, V> extends ChangeEvent {
	private static final long serialVersionUID = 1L;
	private final K key;
	private final V value;

	public PropertyChangeEvent(Object source, ChangeType type, K key, V value) {
		super(source, type);
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}
}
