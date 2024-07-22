package io.basc.framework.event.convert;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.event.DelayableEventDispatcher;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventPushException;
import io.basc.framework.event.EventRegistrationException;
import io.basc.framework.event.EventRegistry;
import io.basc.framework.event.batch.BatchEventDispatcher;
import io.basc.framework.event.batch.BatchEventListener;
import io.basc.framework.event.batch.BatchEventRegistry;
import io.basc.framework.event.batch.DelayableBatchEventDispatcher;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.register.Registration;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

public class ConvertibleEventDispatcher<S, T> implements DelayableBatchEventDispatcher<T> {
	private final EventRegistry<S> source;
	private final Codec<T, S> codec;

	public ConvertibleEventDispatcher(EventRegistry<S> source, Codec<T, S> codec) {
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
	public void publishBatchEvent(Elements<T> events) throws EventPushException {
		if (source instanceof BatchEventDispatcher) {
			((BatchEventDispatcher<S>) source).publishBatchEvent(events.map(codec::encode));
			return;
		}
		events.forEach(this::publishEvent);
	}

	@Override
	public Registration registerListener(EventListener<T> eventListener) throws EventRegistrationException {
		return source.registerListener(new ConvertibleEventListener<>(eventListener, codec::decode));
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<T> batchEventListener)
			throws EventRegistrationException {
		if (source instanceof BatchEventRegistry) {
			BatchEventListener<S> convertibleBatchEventListener = new ConvertibleBatchEventListener<>(
					batchEventListener, (e) -> e.map(codec::decode));
			return ((BatchEventRegistry<S>) source).registerBatchListener(convertibleBatchEventListener);
		}
		return registerListener((e) -> batchEventListener.onEvent(Elements.singleton(e)));
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
	public void publishBatchEvent(Elements<T> events, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		if (source instanceof DelayableBatchEventDispatcher) {
			((DelayableBatchEventDispatcher<S>) source).publishBatchEvent(events.map(codec::encode), delay,
					delayTimeUnit);
			return;
		}

		events.forEach((event) -> publishEvent(event, delay, delayTimeUnit));
	}
}
