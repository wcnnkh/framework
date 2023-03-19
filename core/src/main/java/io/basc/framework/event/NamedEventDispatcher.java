package io.basc.framework.event;

public interface NamedEventDispatcher<K, T> extends NamedEventRegistry<K, T> {
	void publishEvent(K name, T event) throws EventPushException;
}