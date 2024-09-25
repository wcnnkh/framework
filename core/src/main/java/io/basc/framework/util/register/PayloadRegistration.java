package io.basc.framework.util.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;

public interface PayloadRegistration<T> extends Registration {
	public static final PayloadRegistration<?> PAYLOAD_CANCELLED = new PayloadRegisted<>(true, null);

	@SuppressWarnings("unchecked")
	public static <E> PayloadRegisted<E> cancelled() {
		return (PayloadRegisted<E>) CANCELLED;
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