package io.basc.framework.factory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import io.basc.framework.core.OrderComparator;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.Registration;
import io.basc.framework.util.StaticSupplier;

public class ConfigurableServiceLoader<S> implements ServiceLoader<S> {
	private final Set<Supplier<?>> suppliers = new LinkedHashSet<>();

	@Override
	public void reload() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cursor<S> iterator() {
		return Cursor.of(suppliers.stream().map((e) -> (S) e.get()).sorted(OrderComparator.INSTANCE));
	}

	public Registration registerSupplier(Supplier<? extends S> supplier) {
		if (suppliers.add(supplier)) {
			return () -> suppliers.remove(supplier);
		}
		return Registration.EMPTY;
	}

	public Registration register(S service) {
		return registerSupplier(new StaticSupplier<S>(service));
	}
}
