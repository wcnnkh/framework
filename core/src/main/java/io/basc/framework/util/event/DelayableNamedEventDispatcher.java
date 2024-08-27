package io.basc.framework.util.event;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.Elements;
import io.basc.framework.util.register.Registration;

public interface DelayableNamedEventDispatcher<K, E>
		extends DelayableNamedEventPublishService<K, E>, NamedEventDispatcher<K, E> {
	@Override
	Registration registerBatchEventsListener(K name, EventListener<Elements<E>> batchEventsListener)
			throws EventRegistrationException;

	@Override
	void publishBatchEvents(K name, Elements<E> events, long delay, TimeUnit delayTimeUnit) throws EventPushException;
}
