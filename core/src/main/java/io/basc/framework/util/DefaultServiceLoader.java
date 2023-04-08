package io.basc.framework.util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.core.OrderComparator;

public class DefaultServiceLoader<S> implements ConfigurableServiceLoader<S> {
	private volatile LinkedHashSet<ServiceLoader<S>> serviceLoaders;
	@SuppressWarnings("unchecked")
	private Comparator<S> comparator = (Comparator<S>) OrderComparator.INSTANCE;

	public final Comparator<S> getComparator() {
		return comparator;
	}

	public void setComparator(Comparator<S> comparator) {
		this.comparator = comparator;
	}

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
	public Iterator<S> iterator() {
		if (serviceLoaders != null) {
			synchronized (this) {
				if (serviceLoaders != null) {
					Stream<S> stream = serviceLoaders.stream().flatMap((e) -> e.stream());
					if (comparator == null) {
						return stream.distinct().iterator();
					} else {
						return stream.sorted(comparator).distinct().iterator();
					}
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
