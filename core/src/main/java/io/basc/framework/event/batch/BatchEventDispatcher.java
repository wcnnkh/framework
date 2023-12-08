package io.basc.framework.event.batch;

import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventPushException;
import io.basc.framework.util.element.Elements;

public interface BatchEventDispatcher<E> extends BatchEventRegistry<E>, EventDispatcher<E> {
	@Override
	default void publishEvent(E event) throws EventPushException {
		publishBatchEvent(Elements.singleton(event));
	}

	void publishBatchEvent(Elements<E> events) throws EventPushException;
}