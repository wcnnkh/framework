package io.basc.framework.observe.container;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import io.basc.framework.observe.PublishService;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import io.basc.framework.util.actor.EventRegistrationException;
import io.basc.framework.util.actor.batch.BatchEventListener;
import io.basc.framework.util.observe_old.Observable;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.Registrations;
import io.basc.framework.util.register.container.AtomicEntryRegistration;
import io.basc.framework.util.register.container.EntryRegistry;
import lombok.NonNull;

public class ObservableEntryRegistry<K, V, C extends Map<K, AtomicEntryRegistration<K, V>>> extends EntryRegistry<K, V, C>
		implements Observable<ChangeEvent<KeyValue<K, V>>> {
	private final PublishService<ChangeEvent<KeyValue<K, V>>> publishService;

	public ObservableEntryRegistry(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull PublishService<ChangeEvent<KeyValue<K, V>>> publishService) {
		super(containerSupplier, (e) -> new ObservableEntryRegistration<>(e.getKey(), e.getValue(), publishService));
		this.publishService = publishService;
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<ChangeEvent<KeyValue<K, V>>> batchEventListener)
			throws EventRegistrationException {
		return publishService.registerBatchListener(batchEventListener);
	}

	@Override
	public Registrations<AtomicEntryRegistration<K, V>> doRegister(Iterable<? extends KeyValue<K, V>> items,
			BiConsumer<? super C, ? super Elements<AtomicEntryRegistration<K, V>>> writer) throws RegistrationException {
		Registrations<AtomicEntryRegistration<K, V>> registrations = super.doRegister(items, writer);
		Elements<ChangeEvent<KeyValue<K, V>>> events = registrations.getRegistrations()
				.filter((e) -> !e.isInvalid()).map((e) -> new ChangeEvent<>(e, ChangeType.CREATE));
		publishService.publishBatchEvent(events);
		return registrations;
	}
}
