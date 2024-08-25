package io.basc.framework.util.event;

import io.basc.framework.util.element.Elements;

public interface NamedEventPublishService<K, E> {
	void publishBatchEvents(K name, Elements<E> events) throws EventPushException;

	default void publishEvent(K name, E event) throws EventPushException {
		publishBatchEvents(name, Elements.singleton(event));
	}
}
