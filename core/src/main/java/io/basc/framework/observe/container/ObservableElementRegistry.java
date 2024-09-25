package io.basc.framework.observe.container;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import io.basc.framework.observe.PublishService;
import io.basc.framework.util.Elements;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import io.basc.framework.util.actor.EventRegistrationException;
import io.basc.framework.util.actor.batch.BatchEventListener;
import io.basc.framework.util.observe_old.Observable;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.container.AtomicElementRegistration;
import io.basc.framework.util.register.container.ElementRegistry;
import io.basc.framework.util.register.container.ServiceBatchRegistration;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ObservableElementRegistry<E, C extends Collection<AtomicElementRegistration<E>>> extends ElementRegistry<E, C>
		implements Observable<ChangeEvent<E>> {
	private final PublishService<ChangeEvent<E>> publishService;

	public ObservableElementRegistry(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull PublishService<ChangeEvent<E>> publishService) {
		super(containerSupplier, (e) -> new ObservableElementRegistration<>(e, publishService));
		this.publishService = publishService;
	}

	@Override
	protected ServiceBatchRegistration<E, AtomicElementRegistration<E>> newBatchRegistration(
			Iterable<AtomicElementRegistration<E>> registrations) {
		return super.newBatchRegistration(registrations).batch((es) -> () -> {
			Elements<ChangeEvent<E>> events = es
					.map((e) -> new ChangeEvent<>(e.getService(), ChangeType.DELETE));
			publishService.publishBatchEvent(events);
		});
	}

	@Override
	public ServiceBatchRegistration<E, AtomicElementRegistration<E>> doRegister(Iterable<? extends E> items,
			BiConsumer<? super C, ? super Elements<AtomicElementRegistration<E>>> writer) throws RegistrationException {
		ServiceBatchRegistration<E, AtomicElementRegistration<E>> batchRegistration = super.doRegister(items, writer);
		Elements<ChangeEvent<E>> events = batchRegistration.getRegistrations()
				.map((e) -> new ChangeEvent<>(e.getService(), ChangeType.CREATE));
		publishService.publishBatchEvent(events);
		return batchRegistration;
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<ChangeEvent<E>> batchEventListener)
			throws EventRegistrationException {
		return publishService.registerBatchListener(batchEventListener);
	}

}
