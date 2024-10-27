package io.basc.framework.util.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;

public interface PayloadRegistration<T> extends Registration {
	static final PayloadRegistration<?> FAILURE = new PayloadRegisted<>(true, null);
	static final PayloadRegistration<?> SUCCESS = new PayloadRegisted<>(false, null);

	@SuppressWarnings("unchecked")
	public static <E> PayloadRegisted<E> failure() {
		return (PayloadRegisted<E>) FAILURE;
	}

	public static <E> PayloadRegisted<E> failure(E payload) {
		return new PayloadRegisted<E>(true, payload);
	}

	@SuppressWarnings("unchecked")
	public static <E> PayloadRegisted<E> success() {
		return (PayloadRegisted<E>) SUCCESS;
	}

	public static <E> PayloadRegisted<E> success(E payload) {
		return new PayloadRegisted<E>(false, payload);
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