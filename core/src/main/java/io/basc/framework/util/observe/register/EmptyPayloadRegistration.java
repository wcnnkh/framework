package io.basc.framework.util.observe.register;

import io.basc.framework.util.observe.EmptyRegistration;
import io.basc.framework.util.observe.Registration;

public class EmptyPayloadRegistration<T> extends EmptyRegistration implements PayloadRegistration<T> {
	private static final EmptyPayloadRegistration<?> EMPTY = new EmptyPayloadRegistration<>();

	@SuppressWarnings("unchecked")
	public static <E> EmptyPayloadRegistration<E> empty() {
		return (EmptyPayloadRegistration<E>) EMPTY;
	}

	@Override
	public PayloadRegistration<T> and(Registration registration) {
		return PayloadRegistration.super.and(registration);
	}

	@Override
	public T getPayload() {
		return null;
	}
}
