package io.basc.framework.util.register.container;

import java.util.Set;
import java.util.function.Supplier;

import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import lombok.NonNull;

public class SetRegistry<E, C extends Set<ElementRegistration<E>>> extends CollectionRegistry<E, C>
		implements Set<E> {

	public SetRegistry(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull EventPublishService<ChangeEvent<E>> eventPublishService) {
		super(containerSupplier, eventPublishService);
	}
}
