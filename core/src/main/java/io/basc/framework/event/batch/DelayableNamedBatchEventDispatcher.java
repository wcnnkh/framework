package io.basc.framework.event.batch;

import java.util.concurrent.TimeUnit;

import io.basc.framework.event.DelayableNamedEventDispatcher;
import io.basc.framework.event.EventPushException;
import io.basc.framework.util.element.Elements;

public interface DelayableNamedBatchEventDispatcher<K, E>
		extends DelayableNamedEventDispatcher<K, E>, NamedBatchEventDispatcher<K, E> {
	@Override
	default void publishEvent(K name, E event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		publishBatchEvent(name, Elements.singleton(event), delay, delayTimeUnit);
	}

	void publishBatchEvent(K name, Elements<E> events, long delay, TimeUnit delayTimeUnit) throws EventPushException;
}
