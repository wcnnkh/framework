package io.basc.framework.util.event;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.Registration;

public interface DelayableEventDispatcher<T> extends DelayableEventPublishService<T>, EventDispatcher<T> {

	@Override
	void publishBatchEvents(Elements<T> events, long delay, TimeUnit delayTimeUnit) throws EventPushException;

	@Override
	Registration registerBatchEventsListener(EventListener<Elements<T>> batchEventsListener)
			throws EventRegistrationException;

}
