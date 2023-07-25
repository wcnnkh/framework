package io.basc.framework.event;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.event.broadcast.BroadcastDelayableEventDispatcher;
import io.basc.framework.event.unicast.UnicastDelayableEventDispatcher;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.registry.Registration;

public class ConvertibleEventDispatcher<S, T>
		implements UnicastDelayableEventDispatcher<T>, BroadcastDelayableEventDispatcher<T> {
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
	public Registration registerListener(EventListener<T> eventListener) throws EventRegistrationException {
		return source.registerListener((e) -> eventListener.onEvent(codec.decode(e)));
	}

	@Override
	public void publishEvent(T event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		if (source instanceof DelayableEventDispatcher) {
			((DelayableEventDispatcher<S>) source).publishEvent(codec.encode(event), delay, delayTimeUnit);
			return;
		}
		throw new UnsupportedException("Not a DelayableEventDispatcher");
	}
}
