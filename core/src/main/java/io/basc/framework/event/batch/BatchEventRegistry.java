package io.basc.framework.event.batch;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.EventRegistry;
import io.basc.framework.util.Registration;

public interface BatchEventRegistry<E> extends EventRegistry<E> {
	@Override
	default Registration registerListener(EventListener<E> eventListener) throws EventRegistrationException {
		return registerBatchListener(new ForeachBatchEventListener<>(eventListener));
	}

	Registration registerBatchListener(BatchEventListener<E> batchEventListener) throws EventRegistrationException;
}
