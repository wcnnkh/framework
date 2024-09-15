package io.basc.framework.util.register;

import io.basc.framework.util.Registration;
import io.basc.framework.util.RegistrationWrapper;

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
