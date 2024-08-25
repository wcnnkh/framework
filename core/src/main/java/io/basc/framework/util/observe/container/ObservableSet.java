package io.basc.framework.util.observe.container;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import io.basc.framework.util.event.EventDispatcher;
import io.basc.framework.util.event.support.DefaultEventDispatcher;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.container.ElementRegistration;
import lombok.NonNull;

public class ObservableSet<E> extends ObservableCollection<E, Set<ElementRegistration<E>>> implements Set<E> {

	public ObservableSet() {
		this(HashSet::new, new DefaultEventDispatcher<>());
	}

	public ObservableSet(Comparator<? super E> comparator) {
		this(() -> new TreeSet<>(Comparator.comparing(ElementRegistration::getValue, comparator)),
				new DefaultEventDispatcher<>());
	}

	public ObservableSet(@NonNull Supplier<? extends Set<ElementRegistration<E>>> containerSupplier,
			@NonNull EventDispatcher<ChangeEvent<E>> eventDispatcher) {
		super(containerSupplier, eventDispatcher);
	}
}
