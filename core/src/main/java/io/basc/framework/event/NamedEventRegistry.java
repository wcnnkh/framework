package io.basc.framework.event;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.Encoder;
import io.basc.framework.util.Registration;

public interface NamedEventRegistry<K, T> {
	Registration registerListener(K name, EventListener<T> eventListener) throws EventRegistrationException;

	default <R, E> ConvertibleNamedEventDispatcher<K, R, T, E> convert(Encoder<R, K> nameEncoder,
			Codec<E, T> eventCodec) {
		return new ConvertibleNamedEventDispatcher<>(this, nameEncoder, eventCodec);
	}
}
