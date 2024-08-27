package io.basc.framework.util.event.convert;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.event.DelayableEventDispatcher;
import io.basc.framework.util.event.EventDispatcher;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.EventPushException;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.register.Registration;

public class ConvertibleEventDispatcher<S, T> implements DelayableEventDispatcher<T> {
	private final EventDispatcher<S> source;
	private final Codec<T, S> codec;

	public ConvertibleEventDispatcher(EventDispatcher<S> source, Codec<T, S> codec) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(codec != null, "codec");
		this.source = source;
		this.codec = codec;
	}

	@Override
	public void publishEvent(T event) throws EventPushException {
		if (source instanceof EventDispatcher) {
			((EventDispatcher<S>) source).publishEvent(codec.encode(event));
			return;
		}
		throw new UnsupportedException("Not a EventDispatcher");
	}

	@Override
	public void publishBatchEvents(Elements<T> events) throws EventPushException {
		source.publishBatchEvents(events.map(codec::encode));
	}

	@Override
	public Registration registerEventListener(EventListener<T> eventListener) throws EventRegistrationException {
		return source.registerEventListener(new ConvertibleEventListener<>(eventListener, codec::decode));
	}

	@Override
	public Registration registerBatchEventsListener(EventListener<Elements<T>> batchEventsListener)
			throws EventRegistrationException {
		EventListener<Elements<S>> convertibleEventListener = new ConvertibleEventListener<>(batchEventsListener,
				(e) -> e.map(codec::decode));
		return source.registerBatchEventsListener(convertibleEventListener);
	}

	@Override
	public void publishEvent(T event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		if (source instanceof DelayableEventDispatcher) {
			((DelayableEventDispatcher<S>) source).publishEvent(codec.encode(event), delay, delayTimeUnit);
			return;
		}
		throw new UnsupportedException("Not a DelayableEventDispatcher");
	}

	@Override
	public void publishBatchEvents(Elements<T> events, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		if (source instanceof DelayableEventDispatcher) {
			((DelayableEventDispatcher<S>) source).publishBatchEvents(events.map(codec::encode), delay, delayTimeUnit);
			return;
		}

		events.forEach((event) -> publishEvent(event, delay, delayTimeUnit));
	}
}
