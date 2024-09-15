package io.basc.framework.observe.container;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import io.basc.framework.observe.PublishService;
import io.basc.framework.observe.UpdateEvent;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.event.ChangeType;
import io.basc.framework.util.event.EventRegistrationException;
import io.basc.framework.util.event.batch.BatchEventListener;
import io.basc.framework.util.observe_old.Observable;
import io.basc.framework.util.observe_old.Observer;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.container.AtomicElementRegistration;
import io.basc.framework.util.register.container.MultiValueRegistry;
import io.basc.framework.util.register.container.ServiceBatchRegistration;
import lombok.NonNull;

public class ObservableMultiValueMap<K, V>
		extends MultiValueRegistry<K, V, ObservableList<V>, Map<K, ObservableList<V>>>
		implements Observable<ChangeEvent<KeyValue<K, V>>> {
	private static final class InternalElementRegistration<K, V> extends AtomicElementRegistration<V> {
		private final K key;
		private final PublishService<ChangeEvent<KeyValue<K, V>>> publishService;

		public InternalElementRegistration(AtomicElementRegistration<V> elementRegistration, K key,
				PublishService<ChangeEvent<KeyValue<K, V>>> publishService) {
			super(elementRegistration);
			this.key = key;
			this.publishService = publishService;
		}

		public InternalElementRegistration(V initialValue, K key,
				PublishService<ChangeEvent<KeyValue<K, V>>> publishService) {
			super(initialValue);
			this.key = key;
			this.publishService = publishService;
		}

		@Override
		public AtomicElementRegistration<V> combine(@NonNull Registration registration) {
			return new InternalElementRegistration<>(super.combine(registration), key, publishService);
		}

		@Override
		public V setValue(V payload) {
			V oldValue = super.setValue(payload);
			KeyValue<K, V> parent = KeyValue.of(key, oldValue);
			KeyValue<K, V> current = KeyValue.of(key, payload);
			publishService.publishEvent(new UpdateEvent<KeyValue<K, V>>(current, parent));
			return oldValue;
		}
	}

	private static final class InternalObservableList<K, V> extends ObservableList<V> {
		private final K key;
		private final PublishService<ChangeEvent<KeyValue<K, V>>> publishService;

		public InternalObservableList(@NonNull Supplier<? extends List<AtomicElementRegistration<V>>> containerSupplier,
				K key, PublishService<ChangeEvent<KeyValue<K, V>>> publishService) {
			super(containerSupplier, new Observer<>());
			this.key = key;
			this.publishService = publishService;
		}

		@Override
		public ServiceBatchRegistration<V, AtomicElementRegistration<V>> doRegister(Iterable<? extends V> items,
				BiConsumer<? super List<AtomicElementRegistration<V>>, ? super Elements<AtomicElementRegistration<V>>> writer)
				throws RegistrationException {
			ServiceBatchRegistration<V, AtomicElementRegistration<V>> batchRegistration = super.doRegister(items,
					writer);
			Elements<ChangeEvent<KeyValue<K, V>>> events = batchRegistration.getRegistrations()
					.filter((e) -> e.isInvalid())
					.map((e) -> new ChangeEvent<>(KeyValue.of(key, e.getService()), ChangeType.CREATE));
			publishService.publishBatchEvent(events);
			return batchRegistration;
		}

		@Override
		protected ServiceBatchRegistration<V, AtomicElementRegistration<V>> newBatchRegistration(
				Iterable<AtomicElementRegistration<V>> registrations) {
			return super.newBatchRegistration(registrations).batch((es) -> () -> {
				Elements<ChangeEvent<KeyValue<K, V>>> events = es
						.map((e) -> new ChangeEvent<>(KeyValue.of(key, e.getService()), ChangeType.CREATE));
				publishService.publishBatchEvent(events);
			});
		}

		@Override
		protected AtomicElementRegistration<V> newRegistration(V element) {
			return new InternalElementRegistration<>(element, key, publishService);
		}
	}

	private final PublishService<ChangeEvent<KeyValue<K, V>>> publishService;

	public ObservableMultiValueMap(@NonNull Supplier<? extends Map<K, ObservableList<V>>> containerSupplier,
			@NonNull Supplier<? extends List<AtomicElementRegistration<V>>> valuesSupplier,
			PublishService<ChangeEvent<KeyValue<K, V>>> publishService) {
		super(containerSupplier, (key) -> new InternalObservableList<>(valuesSupplier, key, publishService));
		this.publishService = publishService;
	}

	@Override
	public Registration registerBatchListener(BatchEventListener<ChangeEvent<KeyValue<K, V>>> batchEventListener)
			throws EventRegistrationException {
		return publishService.registerBatchListener(batchEventListener);
	}

}
