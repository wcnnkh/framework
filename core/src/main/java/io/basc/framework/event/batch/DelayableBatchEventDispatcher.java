package io.basc.framework.event.batch;

import java.util.concurrent.TimeUnit;

import io.basc.framework.event.DelayableEventDispatcher;
import io.basc.framework.event.EventPushException;
import io.basc.framework.util.element.Elements;

public interface DelayableBatchEventDispatcher<E> extends DelayableEventDispatcher<E>, BatchEventDispatcher<E> {
	@Override
	default void publishEvent(E event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		publishBatchEvent(Elements.singleton(event), delay, delayTimeUnit);
	}

	void publishBatchEvent(Elements<E> events, long delay, TimeUnit delayTimeUnit) throws EventPushException;
}
