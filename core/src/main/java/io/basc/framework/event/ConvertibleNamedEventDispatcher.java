package io.basc.framework.event;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.Encoder;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;

public class ConvertibleNamedEventDispatcher<SK, K, ST, T>
		implements UnicastDelayableNamedEventDispatcher<K, T>, BroadcastDelayableNamedEventDispatcher<K, T> {
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
}
