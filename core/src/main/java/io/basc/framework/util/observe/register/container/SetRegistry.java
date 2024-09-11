package io.basc.framework.util.observe.register.container;

import java.util.Set;
import java.util.function.Supplier;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Publisher;
import io.basc.framework.util.observe.event.ChangeEvent;
import lombok.NonNull;

public class SetRegistry<E, C extends Set<ElementRegistration<E>>> extends CollectionRegistry<E, C> implements Set<E> {

	public SetRegistry(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<E>>> changeEventsPublisher) {
		super(containerSupplier, changeEventsPublisher);
	}
}
