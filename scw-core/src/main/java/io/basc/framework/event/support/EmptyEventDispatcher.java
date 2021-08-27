package io.basc.framework.event.support;

import io.basc.framework.event.Event;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;

public class EmptyEventDispatcher<T extends Event> implements EventDispatcher<T> {

	public EventRegistration registerListener(EventListener<T> eventListener) {
		return EventRegistration.EMPTY;
	}

	public void publishEvent(T event) {
		// ignore
	}
}
