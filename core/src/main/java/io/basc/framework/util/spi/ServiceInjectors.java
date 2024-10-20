package io.basc.framework.util.spi;

import io.basc.framework.util.Registration;

public class ServiceInjectors<S> extends ServiceContainer<ServiceInjector<S>> implements ServiceInjector<S> {

	@Override
	public Registration inject(S service) {
		return Registration.registers(this, (e) -> e.inject(service));
	}
}
