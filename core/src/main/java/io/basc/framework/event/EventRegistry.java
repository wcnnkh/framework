package io.basc.framework.event;

public interface EventRegistry<T extends Event> {
	EventRegistration registerListener(EventListener<T> eventListener);
}
