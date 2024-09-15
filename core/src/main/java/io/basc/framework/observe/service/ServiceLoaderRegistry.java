package io.basc.framework.observe.service;

import io.basc.framework.observe.register.ObservableList;
import io.basc.framework.observe.register.RegistryEvent;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.event.ChangeType;
import io.basc.framework.util.observe_old.Observer;
import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.BrowseableRegistry;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.RegistrationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceLoaderRegistry<S> extends Observer<ChangeEvent<ServiceLoader<? extends S>>>
		implements BrowseableRegistry<ServiceLoader<? extends S>> {
	private final BrowseableRegistry<ServiceLoader<? extends S>> registry;
	private final BrowseableRegistry<S> serviceRegistry;

	public ServiceLoaderRegistry(BrowseableRegistry<S> serviceRegistry) {
		this(new ObservableList<>(), serviceRegistry);
	}

	@Override
	public PayloadRegistration<ServiceLoader<? extends S>> register(ServiceLoader<? extends S> element)
			throws RegistrationException {
		VariableServiceLoader<S> observableServiceLoader = new VariableServiceLoader<>(element, serviceRegistry);
		PayloadRegistration<ServiceLoader<? extends S>> registration = registry.register(observableServiceLoader);
		if (registration.isInvalid()) {
			return registration;
		}

		// 初始化一下
		observableServiceLoader.reload(false);
		registration = registration.and(observableServiceLoader.registerBatchListener((events) -> publishEvent(
				new RegistryEvent<ServiceLoader<? extends S>>(this, ChangeType.UPDATE, element))));
		publishEvent(new RegistryEvent<ServiceLoader<? extends S>>(this, ChangeType.CREATE, element));
		return registration.and(
				() -> publishEvent(new RegistryEvent<ServiceLoader<? extends S>>(this, ChangeType.DELETE, element)));
	}

	@Override
	public void reload() {
		registry.getServices().forEach(ServiceLoader::reload);
	}

	@Override
	public Elements<ServiceLoader<? extends S>> getServices() {
		return registry.getServices();
	}

	@Override
	public BatchRegistration<PayloadRegistration<ServiceLoader<? extends S>>> getRegistrations() {
		return registry.getRegistrations();
	}
}
