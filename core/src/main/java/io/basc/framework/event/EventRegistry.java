package io.basc.framework.event;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.registry.Registration;

public interface EventRegistry<T> {
	Registration registerListener(EventListener<T> eventListener) throws EventRegistrationException;

	default <R> ConvertibleEventDispatcher<T, R> convert(Codec<R, T> codec) {
		return new ConvertibleEventDispatcher<>(this, codec);
	}
}
