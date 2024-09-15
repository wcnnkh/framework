package io.basc.framework.util.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;

public interface PayloadRegistration<T> extends Registration {
	public static final PayloadRegistration<?> PAYLOAD_CANCELLED = new PayloadCancelled<>(null);

	@SuppressWarnings("unchecked")
	public static <E> PayloadCancelled<E> cancelled() {
		return (PayloadCancelled<E>) CANCELLED;
	}

	T getPayload();

	@Override
	default PayloadRegistration<T> and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}
		return new StandardPayloadRegistrationWrapper<>(this, Elements.singleton(registration));
	}
}