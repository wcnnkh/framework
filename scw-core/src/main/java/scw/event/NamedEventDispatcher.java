package scw.event;

public interface NamedEventDispatcher<T extends Event> extends NamedEventRegister<T> {
	void publishEvent(Object name, T event);
}