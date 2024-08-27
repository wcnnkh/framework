package io.basc.framework.util.event;

import io.basc.framework.util.Elements;

public interface EventPublishService<E> {
	void publishBatchEvents(Elements<E> events) throws EventPushException;

	default void publishEvent(E event) throws EventPushException {
		publishBatchEvents(Elements.singleton(event));
	}
}
