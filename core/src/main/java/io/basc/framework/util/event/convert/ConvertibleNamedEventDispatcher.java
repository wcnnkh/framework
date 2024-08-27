package io.basc.framework.util.event.convert;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.Encoder;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.event.DelayableNamedEventDispatcher;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.EventPushException;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.event.NamedEventDispatcher;
import io.basc.framework.util.register.Registration;

public class ConvertibleNamedEventDispatcher<SK, K, ST, T> implements DelayableNamedEventDispatcher<K, T> {
	private final NamedEventDispatcher<SK, ST> source;
	private final Encoder<K, SK> nameEncoder;
	private final Codec<T, ST> eventCodec;

	public ConvertibleNamedEventDispatcher(NamedEventDispatcher<SK, ST> source, Encoder<K, SK> nameEncoder,
			Codec<T, ST> eventCodec) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(nameEncoder != null, "nameEncoder");
		Assert.requiredArgument(eventCodec != null, "eventCodec");
		this.source = source;
		this.nameEncoder = nameEncoder;
		this.eventCodec = eventCodec;
	}

	@Override
	public void publishEvent(K name, T event) throws EventPushException {
		source.publishEvent(nameEncoder.encode(name), eventCodec.encode(event));
	}

	@Override
	public Registration registerEventListener(K name, EventListener<T> eventListener)
			throws EventRegistrationException {
		return source.registerEventListener(nameEncoder.encode(name),
				(e) -> eventListener.onEvent(eventCodec.decode(e)));
	}

	@Override
	public void publishEvent(K name, T event, long delay, TimeUnit delayTimeUnit) throws EventPushException {
		if (source instanceof DelayableNamedEventDispatcher) {
			((DelayableNamedEventDispatcher<SK, ST>) source).publishEvent(nameEncoder.encode(name),
					eventCodec.encode(event), delay, delayTimeUnit);
			return;
		}
		throw new UnsupportedException("Not a DelayableNamedEventDispatcher");
	}

	@Override
	public void publishBatchEvents(K name, Elements<T> events) throws EventPushException {
		if (source instanceof NamedEventDispatcher) {
			((NamedEventDispatcher<SK, ST>) source).publishBatchEvents(nameEncoder.encode(name),
					events.map(eventCodec::encode));
			return;
		}
		events.forEach((event) -> publishEvent(name, event));
	}

	@Override
	public Registration registerBatchEventsListener(K name, EventListener<Elements<T>> batchEventsListener)
			throws EventRegistrationException {
		return source.registerBatchEventsListener(nameEncoder.encode(name),
				new ConvertibleEventListener<>(batchEventsListener, (e) -> e.map(eventCodec::decode)));
	}

	@Override
	public void publishBatchEvents(K name, Elements<T> events, long delay, TimeUnit delayTimeUnit)
			throws EventPushException {
		if (source instanceof DelayableNamedEventDispatcher) {
			((DelayableNamedEventDispatcher<SK, ST>) source).publishBatchEvents(nameEncoder.encode(name),
					events.map(eventCodec::encode), delay, delayTimeUnit);
			return;
		}
		events.forEach((event) -> publishEvent(name, event, delay, delayTimeUnit));
	}
}
