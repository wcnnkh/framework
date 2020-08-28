package scw.event;

public interface BasicEventDispatcher<T extends Event> {
	EventRegistration registerListener(EventListener<T> eventListener);

	void publishEvent(T event);
}