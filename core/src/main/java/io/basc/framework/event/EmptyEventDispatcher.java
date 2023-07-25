package io.basc.framework.event;

import io.basc.framework.util.registry.Registration;

public class EmptyEventDispatcher<T> implements EventDispatcher<T> {

	public Registration registerListener(EventListener<T> eventListener) {
		return Registration.EMPTY;
	}

	public void publishEvent(T event) {
		// ignore
	}
}
