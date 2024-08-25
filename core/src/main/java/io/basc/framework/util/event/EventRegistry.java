package io.basc.framework.util.event;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.Registration;

public interface EventRegistry<E> {

	default Registration registerEventListener(EventListener<E> eventListener) throws EventRegistrationException {
		return registerBatchEventsListener(new ForeachBatchEventsListener<>(eventListener));
	}

	Registration registerBatchEventsListener(EventListener<Elements<E>> batchEventsListener)
			throws EventRegistrationException;
}
