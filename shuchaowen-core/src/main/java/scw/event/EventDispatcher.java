package scw.event;

public interface EventDispatcher {
	EventRegistration registerListener(EventListener<? extends Event> eventListener);

	void publishEvent(Event event);
}
