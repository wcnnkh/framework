package io.basc.framework.util.event;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.Registration;

public interface NamedEventRegistry<K, E> {
	default Registration registerEventListener(K name, EventListener<E> eventListener)
			throws EventRegistrationException {
		return registerBatchEventsListener(name, new ForeachBatchEventsListener<>(eventListener));
	}

	Registration registerBatchEventsListener(K name, EventListener<Elements<E>> batchEventsListener)
			throws EventRegistrationException;
}
