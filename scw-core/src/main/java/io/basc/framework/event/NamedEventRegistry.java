package io.basc.framework.event;

public interface NamedEventRegistry<K, T extends Event> {
	EventRegistration registerListener(K name, EventListener<T> eventListener);
}
