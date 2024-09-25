package io.basc.framework.util.register.container;

import java.util.Set;
import java.util.function.Supplier;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.actor.ChangeEvent;
import lombok.NonNull;

public class SetRegistry<E, C extends Set<ElementRegistration<E>>> extends CollectionRegistry<E, C> implements Set<E> {

	public SetRegistry(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<E>>> changeEventsPublisher) {
		super(containerSupplier, changeEventsPublisher);
	}
}
