package io.basc.framework.util.event.support;

import java.util.concurrent.TimeUnit;

import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.DelayableExecutor;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.event.DelayableEventDispatcher;
import io.basc.framework.util.event.EventDispatcher;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.EventPushException;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.register.Registration;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class DefaultDelayableEventDispatcher<T> implements DelayableEventDispatcher<T> {
	@NonNull
	private final EventDispatcher<T> eventDispatcher;
	@NonNull
	private final DelayableExecutor delayableExecutor;

	public DefaultDelayableEventDispatcher(@NonNull EventDispatcher<T> eventDispatcher,
			@NonNull DelayableExecutor delayableExecutor) {
		this.eventDispatcher = eventDispatcher;
		this.delayableExecutor = delayableExecutor;
	}

	@Override
	public final void publishEvent(T event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		DelayableEventDispatcher.super.publishEvent(event, delay, delayTimeUnit);
	}

	@Override
	public void publishBatchEvents(Elements<T> events, long delay, TimeUnit delayTimeUnit) {
		Assert.requiredArgument(delay >= 0, "delay");
		delayableExecutor.schedule(() -> {
			eventDispatcher.publishBatchEvents(events);
		}, delay, delayTimeUnit);
	}

	@Override
	public void publishBatchEvents(Elements<T> events) throws EventPushException {
		eventDispatcher.publishBatchEvents(events);
	}

	@Override
	public Registration registerBatchEventsListener(EventListener<Elements<T>> batchEventsListener)
			throws EventRegistrationException {
		return eventDispatcher.registerBatchEventsListener(batchEventsListener);
	}
}
