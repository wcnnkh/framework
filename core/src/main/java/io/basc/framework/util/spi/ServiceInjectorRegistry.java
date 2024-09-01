package io.basc.framework.util.spi;

import io.basc.framework.core.OrderComparator;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.container.TreeSetRegistry;
import lombok.NonNull;

public class ServiceInjectorRegistry<S> extends TreeSetRegistry<ServiceInjector<S>> implements ServiceInjector<S> {

	public ServiceInjectorRegistry(@NonNull EventPublishService<ChangeEvent<ServiceInjector<S>>> eventPublishService) {
		super(OrderComparator.INSTANCE, eventPublishService);
	}

	@Override
	public void inject(S service) {
		forEach((e) -> e.inject(service));
	}
}
