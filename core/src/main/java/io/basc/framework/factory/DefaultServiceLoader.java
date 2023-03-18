package io.basc.framework.factory;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

import io.basc.framework.core.OrderComparator;
import io.basc.framework.util.Cursor;
import io.basc.framework.util.Cursors;
import io.basc.framework.util.Registration;
import io.basc.framework.util.StaticSupplier;

public class DefaultServiceLoader<S> implements ConfigurableServiceLoader<S> {
	private volatile LinkedHashSet<ServiceLoader<S>> serviceLoaders;

	@Override
	public void reload() {
		if (serviceLoaders != null) {
			synchronized (this) {
				if (serviceLoaders != null) {
					for (ServiceLoader<S> serviceLoader : serviceLoaders) {
						serviceLoader.reload();
					}
				}
			}
		}
	}

	@Override
	public Cursor<S> iterator() {
		if (serviceLoaders != null) {
			synchronized (this) {
				if (serviceLoaders != null) {
					return Cursor.of(new Cursors<>(serviceLoaders.stream().map((e) -> e.iterator()).iterator()).stream()
							.sorted(OrderComparator.INSTANCE).distinct());
				}
			}
		}
		return Cursor.empty();
	}

	@Override
	public Registration registerLoader(ServiceLoader<S> serviceLoader) {
		if (serviceLoaders == null) {
			synchronized (this) {
				if (serviceLoaders == null) {
					serviceLoaders = new LinkedHashSet<>();
				}
			}
		}

		synchronized (this) {
			if (serviceLoaders.contains(serviceLoader)) {
				return Registration.EMPTY;
			}

			serviceLoaders.add(serviceLoader);
			return () -> unrgister(serviceLoader);
		}
	}

	private void unrgister(ServiceLoader<S> serviceLoader) {
		if (serviceLoaders != null) {
			synchronized (this) {
				if (serviceLoaders != null) {
					serviceLoaders.remove(serviceLoader);
				}
			}
		}
	}

	@Override
	public Registration registerSupplier(Supplier<? extends S> serviceSupplier) {
		SingletonServiceLoader<S> serviceLoader = new SingletonServiceLoader<>(serviceSupplier, serviceSupplier);
		return registerLoader(serviceLoader);
	}

	public Registration register(S service) {
		return registerSupplier(new StaticSupplier<S>(service));
	}
}
