package io.basc.framework.event.support;

import io.basc.framework.event.Event;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.NamedEventDispatcher;

public class EmptyNamedEventDispatcher<K, T extends Event> implements NamedEventDispatcher<K, T> {
	public EventRegistration registerListener(K name, EventListener<T> eventListener) {
		return EventRegistration.EMPTY;
	}

	public void publishEvent(K name, T event) {
		// ignore
	}
}
