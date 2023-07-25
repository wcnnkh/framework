package io.basc.framework.util.spi;

import io.basc.framework.event.support.DefaultDynamicElementRegistry;
import io.basc.framework.util.registry.Registration;

public class ServiceInjectors<S> extends DefaultDynamicElementRegistry<ServiceInjector<? super S>>
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
