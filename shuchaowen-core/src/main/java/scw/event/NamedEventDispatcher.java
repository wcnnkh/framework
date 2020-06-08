package scw.event;

public interface NamedEventDispatcher extends EventDispatcher{
	void unregister(String name);
	
	EventRegistration registerListener(NamedEventListener<? extends NamedEvent> eventListener);

	void publishEvent(NamedEvent event);
}
