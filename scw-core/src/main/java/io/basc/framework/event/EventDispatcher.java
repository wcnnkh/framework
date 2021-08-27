package io.basc.framework.event;

public interface EventDispatcher<T extends Event> extends EventRegistry<T> {
	void publishEvent(T event);
}