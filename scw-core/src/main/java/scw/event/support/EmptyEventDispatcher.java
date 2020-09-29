package scw.event.support;

import scw.event.BasicEventDispatcher;
import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;

public class EmptyEventDispatcher<T extends Event>
		implements BasicEventDispatcher<T>, NamedEventDispatcher<T> {

	public EventRegistration registerListener(EventListener<T> eventListener) {
		return EventRegistration.EMPTY;
	}

	public void publishEvent(T event) {
		// ignore
	}

	public EventRegistration registerListener(Object name, EventListener<T> eventListener) {
		return EventRegistration.EMPTY;
	}

	public void publishEvent(Object name, T event) {
		// ignore
	}
}
