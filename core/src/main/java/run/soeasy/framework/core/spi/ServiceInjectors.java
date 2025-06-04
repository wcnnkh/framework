package run.soeasy.framework.core.spi;

import run.soeasy.framework.core.exchange.Registration;

public class ServiceInjectors<S> extends ServiceContainer<ServiceInjector<? super S>> implements ServiceInjector<S> {

	@Override
	public Registration inject(S service) {
		return Registration.registers(this, (e) -> e.inject(service));
	}
}
