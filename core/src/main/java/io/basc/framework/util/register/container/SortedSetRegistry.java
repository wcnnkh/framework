package io.basc.framework.util.register.container;

import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import lombok.NonNull;

public abstract class SortedSetRegistry<E, C extends SortedSet<ElementRegistration<E>>> extends SetRegistry<E, C>
		implements SortedSet<E> {

	public SortedSetRegistry(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull EventPublishService<ChangeEvent<E>> eventPublishService) {
		super(containerSupplier, eventPublishService);
	}

	public abstract SortedSet<E> subSet(Function<? super C, ? extends Set<ElementRegistration<E>>> getter);

	@Override
	public final SortedSet<E> headSet(E toElement) {
		return subSet((set) -> set.headSet(newElementRegistration(toElement)));
	}

	@Override
	public final SortedSet<E> tailSet(E fromElement) {
		return subSet((set) -> set.tailSet(newElementRegistration(fromElement)));
	}

	@Override
	public final E first() {
		return getPayload((set) -> set.first());
	}

	@Override
	public final E last() {
		return getPayload((set) -> set.last());
	}

}
