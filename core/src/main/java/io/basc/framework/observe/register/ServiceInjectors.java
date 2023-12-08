package io.basc.framework.observe.register;

import io.basc.framework.util.Registration;

public class ServiceInjectors<S> extends ElementRegistry<ServiceInjector<? super S>>
		implements ServiceInjector<S> {

	@Override
	public Registration inject(S service) {
		Registration registration = Registration.EMPTY;
		for (ServiceInjector<? super S> serviceInjector : getServices()) {
			registration = registration.and(serviceInjector.inject(service));
		}
		return registration;
	}
}
