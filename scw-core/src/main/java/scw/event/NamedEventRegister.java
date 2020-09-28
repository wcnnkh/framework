package scw.event;

public interface NamedEventRegister<T extends Event> {
	EventRegistration registerListener(Object name, EventListener<T> eventListener);
}
