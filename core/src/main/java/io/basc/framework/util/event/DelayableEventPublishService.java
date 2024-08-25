package io.basc.framework.util.event;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.element.Elements;

public interface DelayableEventPublishService<E> extends EventPublishService<E> {

	@Override
	default void publishBatchEvents(Elements<E> events) throws EventPushException {
		publishBatchEvents(events, 0, TimeUnit.SECONDS);
	}

	void publishBatchEvents(Elements<E> events, long delay, TimeUnit delayTimeUnit) throws EventPushException;

	default void publishEvent(E event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		publishBatchEvents(Elements.singleton(event), delay, delayTimeUnit);
	}
}
