package io.basc.framework.event.batch;

import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.NamedEventRegistry;
import io.basc.framework.util.register.Registration;

public interface NamedBatchEventRegistry<K, E> extends NamedEventRegistry<K, E> {
	@Override
	default Registration registerListener(K name, EventListener<E> eventListener) throws EventRegistrationException {
		return registerBatchListener(name, new ForeachBatchEventListener<>(eventListener));
	}

	Registration registerBatchListener(K name, BatchEventListener<E> batchEventListener)
			throws EventRegistrationException;
}
