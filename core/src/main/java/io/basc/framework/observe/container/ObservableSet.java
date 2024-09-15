package io.basc.framework.observe.container;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import io.basc.framework.observe.PublishService;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.observe_old.Observer;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.container.AtomicElementRegistration;
import lombok.NonNull;

public class ObservableSet<E> extends ObservableElementRegistry<E, Set<AtomicElementRegistration<E>>>
		implements Set<E> {

	public ObservableSet() {
		this(HashSet::new, new Observer<>());
	}

	public ObservableSet(Comparator<? super E> comparator) {
		this(() -> new TreeSet<>(Comparator.comparing(PayloadRegistration::getService, comparator)), new Observer<>());
	}

	public ObservableSet(@NonNull Supplier<? extends Set<AtomicElementRegistration<E>>> containerSupplier,
			@NonNull PublishService<ChangeEvent<E>> publishService) {
		super(containerSupplier, publishService);
	}
}
