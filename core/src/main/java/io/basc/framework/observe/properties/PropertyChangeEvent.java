package io.basc.framework.observe.properties;

import io.basc.framework.event.Event;

public class PropertyChangeEvent<K> extends Event {
	private static final long serialVersionUID = 1L;
	private final K key;

	public PropertyChangeEvent(Object source, K key) {
		super(source);
		this.key = key;
	}

	public K getKey() {
		return key;
	}
}
