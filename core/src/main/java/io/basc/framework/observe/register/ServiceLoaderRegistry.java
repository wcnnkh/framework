package io.basc.framework.observe.register;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.Observer;
import io.basc.framework.observe.container.ServiceRegistry;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;
import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.RegistrationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServiceLoaderRegistry<S> extends Observer<RegistryEvent<ServiceLoader<? extends S>>>
		implements ServiceRegistry<ServiceLoader<? extends S>> {
	private final ServiceRegistry<ServiceLoader<? extends S>> registry;
	private final ServiceRegistry<S> serviceRegistry;

	public ServiceLoaderRegistry(ServiceRegistry<S> serviceRegistry) {
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
