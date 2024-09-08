package io.basc.framework.observe.register;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import io.basc.framework.util.observe.register.PayloadRegistration;
import lombok.NonNull;

public class ObservableSet<E> extends ObservableCollection<E, Set<PayloadRegistration<E>>> implements Set<E> {

	public ObservableSet() {
		this(() -> new LinkedHashSet<>());
	}

	public ObservableSet(Comparator<? super E> comparator) {
		this(() -> new TreeSet<>(Comparator.comparing(PayloadRegistration::getPayload, comparator)));
	}

	public ObservableSet(@NonNull Supplier<? extends Set<PayloadRegistration<E>>> collectioSupplier) {
		super(collectioSupplier);
	}
}
