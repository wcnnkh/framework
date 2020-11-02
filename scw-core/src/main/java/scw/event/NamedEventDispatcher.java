package scw.event;

public interface NamedEventDispatcher<K, T extends Event> extends NamedEventRegister<K, T> {
	void publishEvent(K name, T event);
}