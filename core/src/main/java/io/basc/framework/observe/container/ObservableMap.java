package io.basc.framework.observe.container;

import java.util.Map;
import java.util.function.Supplier;

import io.basc.framework.observe.PublishService;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.register.container.AtomicEntryRegistration;
import io.basc.framework.util.observe_old.Observer;
import lombok.NonNull;

public class ObservableMap<K, V, C extends Map<K, AtomicEntryRegistration<K, V>>> extends ObservableEntryRegistry<K, V, C> {

	public ObservableMap(@NonNull Supplier<? extends C> containerSupplier) {
		this(containerSupplier, new Observer<>());
	}

	public ObservableMap(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull PublishService<ChangeEvent<KeyValue<K, V>>> publishService) {
		super(containerSupplier, publishService);
	}
}
