package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.RegistrationWrapper;

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