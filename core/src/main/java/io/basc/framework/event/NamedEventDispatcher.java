package io.basc.framework.event;

import io.basc.framework.util.Elements;

public interface NamedEventDispatcher<K, T> extends NamedEventRegistry<K, T> {
	void publishEvent(K name, T event) throws EventPushException;

	default void batchPublishEvent(Elements<K> names, T event) throws EventPushException {
		for (K name : names) {
			publishEvent(name, event);
		}
	}
}