package run.soeasy.framework.core.exchange.container.collection;

import java.util.ArrayList;
import java.util.RandomAccess;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.function.ThrowingSupplier;

public class ArrayListContainer<E> extends ListContainer<E, ArrayList<ElementRegistration<E>>> implements RandomAccess {
	public ArrayListContainer() {
		this(ArrayList::new);
	}

	public ArrayListContainer(int initialCapacity) {
		this(() -> new ArrayList<>(initialCapacity));
	}

	public ArrayListContainer(
			@NonNull ThrowingSupplier<? extends ArrayList<ElementRegistration<E>>, ? extends RuntimeException> containerSource) {
		super(containerSource);
	}
}
