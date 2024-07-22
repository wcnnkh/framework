package io.basc.framework.event.empty;

import java.util.concurrent.TimeUnit;

import io.basc.framework.event.EventPushException;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.event.batch.DelayableBatchEventDispatcher;
import io.basc.framework.register.Registration;
import io.basc.framework.util.element.Elements;

public class EmptyEventDispatcher<E> implements DelayableBatchEventDispatcher<E> {

	@Override
	public void publishBatchEvent(Elements<E> events) throws EventPushException {
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<E> batchEventListener)
			throws EventRegistrationException {
		return Registration.EMPTY;
	}

	@Override
	public void publishBatchEvent(Elements<E> events, long delay, TimeUnit delayTimeUnit) throws EventPushException {
	}

}
