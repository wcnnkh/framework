package io.basc.framework.util.register.container;

import java.util.ArrayList;
import java.util.RandomAccess;

import io.basc.framework.util.function.Supplier;
import lombok.NonNull;

public class ArrayListContainer<E> extends ListContainer<E, ArrayList<ElementRegistration<E>>> implements RandomAccess {
	public ArrayListContainer() {
		this(ArrayList::new);
	}

	public ArrayListContainer(int initialCapacity) {
		this(() -> new ArrayList<>(initialCapacity));
	}

	public ArrayListContainer(
			@NonNull Supplier<? extends ArrayList<ElementRegistration<E>>, ? extends RuntimeException> containerSource) {
		super(containerSource);
	}
}
