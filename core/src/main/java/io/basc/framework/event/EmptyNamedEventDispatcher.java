package io.basc.framework.event;

import io.basc.framework.util.Registration;

public class EmptyNamedEventDispatcher<K, T> implements NamedEventDispatcher<K, T> {
	public Registration registerListener(K name, EventListener<T> eventListener) {
		return Registration.EMPTY;
	}

	public void publishEvent(K name, T event) {
	}
}
