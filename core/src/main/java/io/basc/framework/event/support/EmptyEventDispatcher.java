package io.basc.framework.event.support;

import io.basc.framework.event.Event;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.util.Registration;

public class EmptyEventDispatcher<T extends Event> implements EventDispatcher<T> {

	public Registration registerListener(EventListener<T> eventListener) {
		return Registration.EMPTY;
	}

	public void publishEvent(T event) {
		// ignore
	}
}
