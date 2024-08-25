package io.basc.framework.util.event;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.Registration;

public interface NamedEventDispatcher<K, E> extends NamedEventPublishService<K, E>, NamedEventRegistry<K, E> {

	@Override
	void publishBatchEvents(K name, Elements<E> events) throws EventPushException;

	@Override
	Registration registerBatchEventsListener(K name, EventListener<Elements<E>> batchEventsListener)
			throws EventRegistrationException;
}