package io.basc.framework.util.spi;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import lombok.NonNull;

public class InjectableServices<S> extends Services<S> {
	private final ServiceInjectors<S> injectors = new ServiceInjectors<>(this::onServiceInjectorEvents);

	public InjectableServices(@NonNull Publisher<? super Elements<ChangeEvent<S>>> changeEventsPublisher) {
		super(changeEventsPublisher);
	}

	protected Receipt onServiceInjectorEvents(Elements<ChangeEvent<ServiceInjector<S>>> events) {
		for (ChangeEvent<ServiceInjector<S>> event : events) {
			getElements().forEach((service) -> {
				if (event.getChangeType() != ChangeType.CREATE) {
					return;
				}

				event.getSource().inject(service);
			});
		}
		return Receipt.success();
	}

	public ServiceInjectors<S> getInjectors() {
		return injectors;
	}
}
