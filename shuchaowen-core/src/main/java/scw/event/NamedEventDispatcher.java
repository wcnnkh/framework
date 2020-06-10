package scw.event;

public interface NamedEventDispatcher extends EventDispatcher{
	void unregister(String name);
	
	EventRegistration registerListener(NamedEventListener<? extends Event> eventListener);

	EventRegistration registerListener(String name, EventListener<? extends Event> eventListener);
	
	void publishEvent(String name, Event event);
	
	void publishEvent(NamedEvent event);
}
