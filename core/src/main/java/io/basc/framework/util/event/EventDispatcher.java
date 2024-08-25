package io.basc.framework.util.event;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.Registration;

public interface EventDispatcher<T> extends EventRegistry<T>, EventPublishService<T> {

	@Override
	Registration registerBatchEventsListener(EventListener<Elements<T>> batchEventsListener)
			throws EventRegistrationException;

	@Override
	void publishBatchEvents(Elements<T> events) throws EventPushException;
}