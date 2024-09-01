package io.basc.framework.util.register.container;

import java.util.ArrayList;
import java.util.function.Supplier;

import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import lombok.NonNull;

public class ArrayListRegistry<E> extends ListRegistry<E, ArrayList<ElementRegistration<E>>> {

	public ArrayListRegistry(@NonNull EventPublishService<ChangeEvent<E>> eventPublishService) {
		this(ArrayList::new, eventPublishService);
	}

	public ArrayListRegistry(@NonNull Supplier<? extends ArrayList<ElementRegistration<E>>> containerSupplier,
			@NonNull EventPublishService<ChangeEvent<E>> eventPublishService) {
		super(containerSupplier, eventPublishService);
	}
}
