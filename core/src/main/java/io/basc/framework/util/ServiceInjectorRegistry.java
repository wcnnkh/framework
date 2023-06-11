package io.basc.framework.util;

import io.basc.framework.event.support.DefaultDynamicElementRegistry;

public class ServiceInjectorRegistry<S> extends DefaultDynamicElementRegistry<ServiceInjector<? super S>>
		implements ServiceInjector<S> {

	@Override
	public Registration inject(S service) {
		Registration registration = Registration.EMPTY;
		for (ServiceInjector<? super S> serviceInjector : getElements()) {
			registration = registration.and(serviceInjector.inject(service));
		}
		return registration;
	}
}
