package io.basc.framework.util.observe.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Registration;

public interface PayloadRegistration<T> extends Registration {
	T getPayload();

	@Override
	default PayloadRegistration<T> and(Registration registration) {
		if (registration == null || registration == EMPTY) {
			return this;
		}
		return new StandardPayloadRegistrationWrapper<>(this, Elements.singleton(registration));
	}
}