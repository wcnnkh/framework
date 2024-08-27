package io.basc.framework.util.event.empty;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.Elements;
import io.basc.framework.util.event.DelayableEventDispatcher;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.EventPushException;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.register.Registration;

public class EmptyEventDispatcher<E> implements DelayableEventDispatcher<E> {
	private static final EmptyEventDispatcher<?> EMPTY = new EmptyEventDispatcher<>();

	@SuppressWarnings("unchecked")
	public static <T> DelayableEventDispatcher<T> empty() {
		return (DelayableEventDispatcher<T>) EMPTY;
	}

	@Override
	public Registration registerBatchEventsListener(EventListener<Elements<E>> batchEventsListener)
			throws EventRegistrationException {
		return Registration.EMPTY;
	}

	@Override
	public void publishBatchEvents(Elements<E> events, long delay, TimeUnit delayTimeUnit) throws EventPushException {
	}

}
