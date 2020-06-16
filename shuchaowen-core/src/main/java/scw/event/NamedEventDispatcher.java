package scw.event;

public interface NamedEventDispatcher<T extends Event> extends EventDispatcher<T>{
	void unregister(String name);

	EventRegistration registerListener(String name, EventListener<T> eventListener);

	void publishEvent(String name, T event);
}