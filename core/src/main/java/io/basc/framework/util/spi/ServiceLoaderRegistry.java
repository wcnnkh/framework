package io.basc.framework.util.spi;

import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.ServiceLoaderWrapper;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.event.empty.EmptyEventDispatcher;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.register.container.ElementRegistration;
import io.basc.framework.util.observe.register.container.ElementRegistry;
import lombok.NonNull;

public class ServiceLoaderRegistry<S>
		extends ElementRegistry<ServiceLoader<S>, Set<ElementRegistration<ServiceLoader<S>>>>
		implements ServiceLoaderWrapper<S, Elements<S>> {

	public ServiceLoaderRegistry() {
		this(EmptyEventDispatcher.empty());
	}

	public ServiceLoaderRegistry(@NonNull EventPublishService<ChangeEvent<ServiceLoader<S>>> eventPublishService) {
		super(LinkedHashSet::new, eventPublishService);
	}

	@Override
	public Elements<S> getSource() {
		return getElements().flatMap((e) -> e);
	}

	@Override
	public void reload() {
		getElements().forEach((e) -> e.reload());
	}
}
