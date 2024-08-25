package io.basc.framework.util.event.empty;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.event.DelayableNamedEventDispatcher;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.EventPushException;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.register.Registration;

public class EmpytNamedEventDispatcher<K, E> implements DelayableNamedEventDispatcher<K, E> {
	private static final EmpytNamedEventDispatcher<?, ?> EMPTY = new EmpytNamedEventDispatcher<>();

	@SuppressWarnings("unchecked")
	public static <N, T> DelayableNamedEventDispatcher<N, T> empty() {
		return (DelayableNamedEventDispatcher<N, T>) EMPTY;
	}

	@Override
	public Registration registerBatchEventsListener(K name, EventListener<Elements<E>> batchEventsListener)
			throws EventRegistrationException {
		return Registration.EMPTY;
	}

	@Override
	public void publishBatchEvents(K name, Elements<E> events, long delay, TimeUnit delayTimeUnit)
			throws EventPushException {
	}
}
