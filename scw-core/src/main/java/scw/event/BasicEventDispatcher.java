package scw.event;

public interface BasicEventDispatcher<T extends Event> extends BasicEventRegister<T> {
	void publishEvent(T event);
}