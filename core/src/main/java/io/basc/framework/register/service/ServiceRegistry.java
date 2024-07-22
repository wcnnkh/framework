package io.basc.framework.register.service;

import io.basc.framework.register.PayloadRegistration;
import io.basc.framework.register.Registration;
import io.basc.framework.register.RegistrationException;
import io.basc.framework.register.Registrations;
import io.basc.framework.register.Registry;

public interface ServiceRegistry<S> extends Registry<PayloadRegistration<S>> {
	PayloadRegistration<S> register(S element) throws RegistrationException;

	Registrations<PayloadRegistration<S>> getRegistrations();

	default Registrations<PayloadRegistration<S>> registers(Iterable<? extends S> elements)
			throws RegistrationException {
		return Registration.registers(elements, this::register);
	}
}
