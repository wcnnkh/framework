package io.basc.framework.util.register;

public interface PayloadRegistration<T> extends Registration {
	T getPayload();

	@Override
	default PayloadRegistration<T> and(Registration registration) {
		if (registration == null || registration == EMPTY) {
			return this;
		}
		return new CombinablePayloadRegistration<>(this, registration);
	}
}