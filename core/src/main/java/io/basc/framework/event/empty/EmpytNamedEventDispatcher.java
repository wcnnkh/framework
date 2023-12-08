package io.basc.framework.event.empty;

import java.util.concurrent.TimeUnit;

import io.basc.framework.event.EventPushException;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.event.batch.DelayableNamedBatchEventDispatcher;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;

public class EmpytNamedEventDispatcher<K, E> implements DelayableNamedBatchEventDispatcher<K, E> {

	@Override
	public void publishBatchEvent(K name, Elements<E> events) throws EventPushException {
	}

	@Override
	public Registration registerBatchListener(K name, BatchEventListener<E> batchEventListener)
			throws EventRegistrationException {
		return Registration.EMPTY;
	}

	@Override
	public void publishBatchEvent(K name, Elements<E> events, long delay, TimeUnit delayTimeUnit)
			throws EventPushException {
	}

}
