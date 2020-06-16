package scw.event;

public interface EventDispatcher<T extends Event> extends BasicEventDispatcher<T>{
	void unregister(Class<? extends T> eventType);

	<E extends T> EventRegistration registerListener(Class<E> eventType, EventListener<E> eventListener);

	<E extends T> void publishEvent(Class<E> eventType, E event);
}