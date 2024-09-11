package io.basc.framework.util.observe.register.container;

import java.util.ArrayList;
import java.util.RandomAccess;
import java.util.function.Supplier;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Publisher;
import io.basc.framework.util.observe.event.ChangeEvent;
import lombok.NonNull;

public class ArrayListRegistry<E> extends ListRegistry<E, ArrayList<ElementRegistration<E>>> implements RandomAccess {

	public ArrayListRegistry() {
		this(ArrayList::new, Publisher.empty());
	}

	public ArrayListRegistry(@NonNull Supplier<? extends ArrayList<ElementRegistration<E>>> containerSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<E>>> changeEventsProducter) {
		super(containerSupplier, changeEventsProducter);
	}
}
