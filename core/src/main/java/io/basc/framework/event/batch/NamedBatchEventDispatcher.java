package io.basc.framework.event.batch;

import io.basc.framework.event.EventPushException;
import io.basc.framework.event.NamedEventDispatcher;
import io.basc.framework.util.element.Elements;

public interface NamedBatchEventDispatcher<K, E> extends NamedEventDispatcher<K, E>, NamedBatchEventRegistry<K, E> {
	@Override
	default void publishEvent(K name, E event) throws EventPushException {
		publishBatchEvent(name, Elements.singleton(event));
	}

	void publishBatchEvent(K name, Elements<E> events) throws EventPushException;
}
