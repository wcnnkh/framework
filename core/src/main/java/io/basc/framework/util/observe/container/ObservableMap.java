package io.basc.framework.util.observe.container;

import java.util.Map;
import java.util.function.Supplier;

import io.basc.framework.util.KeyValue;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.container.EntryRegistration;
import io.basc.framework.util.register.container.EntryRegistry;
import lombok.NonNull;

public class ObservableMap<K, V> extends EntryRegistry<K, V, Map<K, EntryRegistration<K, V>>> {

	public ObservableMap(Supplier<? extends Map<K, EntryRegistration<K, V>>> containerSupplier,
			@NonNull EventPublishService<ChangeEvent<KeyValue<K, V>>> eventPublishService) {
		super(containerSupplier, eventPublishService);
	}

}
