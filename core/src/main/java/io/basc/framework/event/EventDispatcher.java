package io.basc.framework.event;

public interface EventDispatcher<T> extends EventRegistry<T> {
	void publishEvent(T event) throws EventPushException;
}