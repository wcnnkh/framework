package scw.event.support;

import scw.event.Event;
import scw.event.EventDispatcher;
import scw.event.EventListener;
import scw.event.EventRegistration;

public class EmptyEventDispatcher<T extends Event> implements
		EventDispatcher<T> {

	public EventRegistration registerListener(EventListener<T> eventListener) {
		return new EmptyEventRegistration();
	}

	public void publishEvent(T event) {
		// ignore
	}

	public void unregister(Class<? extends T> eventType) {
		// ignore
	}

	public <E extends T> EventRegistration registerListener(Class<E> eventType,
			EventListener<E> eventListener) {
		return new EmptyEventRegistration();
	}

	public <E extends T> void publishEvent(Class<E> eventType, E event) {
		// ignore
	}

	public void unregister(String name) {
		// ignore
	}

	public EventRegistration registerListener(String name,
			EventListener<T> eventListener) {
		return new EmptyEventRegistration();
	}

	public void publishEvent(String name, T event) {
		// ignore
	}

}
