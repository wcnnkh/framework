package scw.event;

public interface BasicEventRegister<T extends Event> {
	EventRegistration registerListener(EventListener<T> eventListener);
}
