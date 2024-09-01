package io.basc.framework.util.register;

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
