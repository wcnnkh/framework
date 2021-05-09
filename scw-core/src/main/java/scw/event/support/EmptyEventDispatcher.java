package scw.event.support;

import scw.event.EventDispatcher;
import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;

public class EmptyEventDispatcher<T extends Event> implements EventDispatcher<T> {

	public EventRegistration registerListener(EventListener<T> eventListener) {
		return EventRegistration.EMPTY;
	}

	public void publishEvent(T event) {
		// ignore
	}
}
