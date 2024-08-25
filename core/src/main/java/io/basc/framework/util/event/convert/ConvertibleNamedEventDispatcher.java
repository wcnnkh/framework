package io.basc.framework.util.event.convert;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.Encoder;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.event.DelayableNamedEventDispatcher;
import io.basc.framework.util.event.EventListener;
import io.basc.framework.util.event.EventPushException;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.event.NamedEventDispatcher;
import io.basc.framework.util.event.NamedEventRegistry;
import io.basc.framework.util.event.batch.BatchEventListener;
import io.basc.framework.util.event.batch.DelayableNamedBatchEventDispatcher;
import io.basc.framework.util.event.batch.NamedBatchEventDispatcher;
import io.basc.framework.util.event.batch.NamedBatchEventRegistry;
import io.basc.framework.util.register.Registration;

public class ConvertibleNamedEventDispatcher<SK, K, ST, T> implements DelayableNamedBatchEventDispatcher<K, T> {
	private final NamedEventRegistry<SK, ST> source;
	private final Encoder<K, SK> nameEncoder;
	private final Codec<T, ST> eventCodec;

	public ConvertibleNamedEventDispatcher(NamedEventRegistry<SK, ST> source, Encoder<K, SK> nameEncoder,
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
		if (source instanceof NamedEventDispatcher) {
			((NamedEventDispatcher<SK, ST>) source).publishEvent(nameEncoder.encode(name), eventCodec.encode(event));
			return;
		}
		throw new UnsupportedException("Not a NamedEventDispatcher");
	}

	@Override
	public Registration registerListener(K name, EventListener<T> eventListener) throws EventRegistrationException {
		return source.registerListener(nameEncoder.encode(name), (e) -> eventListener.onEvent(eventCodec.decode(e)));
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
	public void publishBatchEvent(K name, Elements<T> events) throws EventPushException {
		if (source instanceof NamedBatchEventDispatcher) {
			((NamedBatchEventDispatcher<SK, ST>) source).publishBatchEvent(nameEncoder.encode(name),
					events.map(eventCodec::encode));
			return;
		}
		events.forEach((event) -> publishEvent(name, event));
	}

	@Override
	public Registration registerBatchListener(K name, BatchEventListener<T> batchEventListener)
			throws EventRegistrationException {
		if (source instanceof NamedBatchEventRegistry) {
			return ((NamedBatchEventRegistry<SK, ST>) source).registerBatchListener(nameEncoder.encode(name),
					new ConvertibleBatchEventListener<>(batchEventListener, (e) -> e.map(eventCodec::decode)));
		}
		return registerListener(name, (e) -> batchEventListener.onEvent(Elements.singleton(e)));
	}

	@Override
	public void publishBatchEvent(K name, Elements<T> events, long delay, TimeUnit delayTimeUnit)
			throws EventPushException {
		if (source instanceof DelayableNamedBatchEventDispatcher) {
			((DelayableNamedBatchEventDispatcher<SK, ST>) source).publishBatchEvent(nameEncoder.encode(name),
					events.map(eventCodec::encode), delay, delayTimeUnit);
			return;
		}
		events.forEach((event) -> publishEvent(name, event, delay, delayTimeUnit));
	}
}
