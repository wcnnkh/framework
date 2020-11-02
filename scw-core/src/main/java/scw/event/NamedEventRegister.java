package scw.event;

public interface NamedEventRegister<K, T extends Event> {
	EventRegistration registerListener(K name, EventListener<T> eventListener);
}
