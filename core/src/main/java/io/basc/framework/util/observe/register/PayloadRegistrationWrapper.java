package io.basc.framework.util.observe.register;

import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.RegistrationWrapper;

public interface PayloadRegistrationWrapper<T, W extends PayloadRegistration<T>>
		extends RegistrationWrapper<W>, PayloadRegistration<T> {

	@Override
	default PayloadRegistration<T> and(Registration registration) {
		return getSource().and(registration);
	}

	@Override
	default T getPayload() {
		return getSource().getPayload();
	}
}
