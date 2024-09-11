package io.basc.framework.observe.container;

import io.basc.framework.observe.PublishService;
import io.basc.framework.observe.UpdateEvent;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.event.ChangeType;
import io.basc.framework.util.observe.register.RegistrationException;
import io.basc.framework.util.observe.register.container.AtomicEntryRegistration;
import lombok.NonNull;

public class ObservableEntryRegistration<K, V> extends AtomicEntryRegistration<K, V> {
	private final PublishService<ChangeEvent<KeyValue<K, V>>> publishService;

	public ObservableEntryRegistration(K key, V value,
			@NonNull PublishService<ChangeEvent<KeyValue<K, V>>> publishService) {
		super(key, value);
		this.publishService = publishService;
	}

	@Override
	public void deregister(Runnable runnable) throws RegistrationException {
		super.deregister(() -> {
			try {
				runnable.run();
			} finally {
				publishService.publishEvent(new ChangeEvent<KeyValue<K, V>>(this, ChangeType.DELETE));
			}
		});
	}

	@Override
	public V setValue(V value) {
		V oldValue = super.setValue(value);
		KeyValue<K, V> parent = KeyValue.of(getKey(), oldValue);
		KeyValue<K, V> current = KeyValue.of(getKey(), value);
		publishService.publishEvent(new UpdateEvent<KeyValue<K, V>>(current, parent));
		return oldValue;
	}
}
