package io.basc.framework.util.spi;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.register.container.TreeSetRegistry;
import lombok.NonNull;

public class ServiceInjectorRegistry<S> extends TreeSetRegistry<ServiceInjector<S>> implements ServiceInjector<S> {

	public ServiceInjectorRegistry(@NonNull Publisher<? super Elements<ChangeEvent<ServiceInjector<S>>>> publisher) {
		super(publisher);
	}

	@Override
	public void inject(S service) {
		forEach((e) -> e.inject(service));
	}
}
