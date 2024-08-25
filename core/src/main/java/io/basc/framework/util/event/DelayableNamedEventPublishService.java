package io.basc.framework.util.event;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.element.Elements;

public interface DelayableNamedEventPublishService<K, E> extends NamedEventPublishService<K, E> {

	@Override
	default void publishBatchEvents(K name, Elements<E> events) throws EventPushException {
		publishBatchEvents(name, events, 0, TimeUnit.SECONDS);
	}

	default void publishEvent(K name, E event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		publishBatchEvents(name, Elements.singleton(event), delay, delayTimeUnit);
	}

	void publishBatchEvents(K name, Elements<E> events, long delay, TimeUnit delayTimeUnit) throws EventPushException;
}
