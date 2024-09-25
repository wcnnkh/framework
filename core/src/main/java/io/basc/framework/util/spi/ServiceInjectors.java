package io.basc.framework.util.spi;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Registration;
import io.basc.framework.util.actor.ChangeEvent;
import lombok.NonNull;

public class ServiceInjectors<S> extends Services<ServiceInjector<S>> implements ServiceInjector<S> {

	public ServiceInjectors(@NonNull Publisher<? super Elements<ChangeEvent<ServiceInjector<S>>>> publisher) {
		super(publisher);
	}

	@Override
	public Registration inject(S service) {
		return Registration.registers(this, (e) -> e.inject(service));
	}
}
