package scw.event;

public interface NamedEventDispatcher<T extends Event> {
	void unregister(Object name);

	EventRegistration registerListener(Object name, EventListener<T> eventListener);

	void publishEvent(Object name, T event);
}
