package io.basc.framework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.core.OrderComparator;
import io.basc.framework.lang.Nullable;

public class Services<T> implements Iterable<T>, Consumer<T> {
	private T afterService;
	private T beforeService;
	private volatile Services<Consumer<T>> consumers;
	private volatile Collection<T> services;
	private final Supplier<Collection<T>> supplier;

	public Services() {
		this(null);
	}

	public Services(@Nullable Supplier<Collection<T>> supplier) {
		this.supplier = supplier == null ? (() -> new ArrayList<>(8)) : supplier;
	}

	@Override
	public void accept(T service) {
		if (consumers == null) {
			synchronized (this) {
				if (consumers == null) {
					return;
				}
			}
		}

		for (Consumer<T> consumer : consumers) {
			consumer.accept(service);
		}
	}

	public final boolean addService(T service) {
		if (service == null) {
			return false;
		}

		accept(service);
		synchronized (this) {
			if (services == null) {
				services = supplier.get();
			}
			boolean success = addService(service, services);
			postProcessing(services);
			return success;
		}
	}

	protected boolean addService(T service, Collection<T> targetServices) {
		return targetServices.add(service);
	}

	public final boolean addServices(Iterable<? extends T> services) {
		if (services == null) {
			return false;
		}

		boolean success = false;
		synchronized (this) {
			for (T service : services) {
				if (service == null) {
					continue;
				}

				accept(service);
				if (this.services == null) {
					this.services = supplier.get();
				}

				if (addService(service, this.services) && !success) {
					success = true;
				}
			}

			if (this.services != null) {
				postProcessing(this.services);
			}
		}
		return success;
	}

	public void clear() {
		if (services == null) {
			return;
		}

		synchronized (this) {
			services.clear();
		}
	}

	public final T getAfterService() {
		return afterService;
	}

	public final T getBeforeService() {
		return beforeService;
	}

	public final Services<Consumer<T>> getConsumers() {
		if (consumers == null) {
			synchronized (this) {
				if (consumers == null) {
					consumers = new Services<>();
				}
			}
		}
		return consumers;
	}

	public final Collection<T> getTargetServices() {
		return this.services == null ? Collections.emptyList() : Collections.unmodifiableCollection(this.services);
	}

	public boolean isEmpty() {
		return afterService == null && beforeService == null && CollectionUtils.isEmpty(services);
	}

	@Override
	public Iterator<T> iterator() {
		T before = getBeforeService();
		T after = getAfterService();
		return new MultiIterator<T>(before == null ? Collections.emptyIterator() : Arrays.asList(before).iterator(),
				services == null ? Collections.emptyIterator() : services.iterator(),
				after == null ? Collections.emptyIterator() : Arrays.asList(after).iterator());
	}

	public void postProcessing(Collection<T> service) {
		if (services.getClass() == ArrayList.class) {
			// 如果是使用的ArrayList说明是没有经过自定义的
			((ArrayList<T>) services).sort(OrderComparator.INSTANCE);
		}
	}

	public void setAfterService(T afterService) {
		this.afterService = afterService;
	}

	public void setBeforeService(T beforeService) {
		this.beforeService = beforeService;
	}

	public Stream<T> stream() {
		return Streams.stream(iterator());
	}

	@Override
	public String toString() {
		return stream().collect(Collectors.toList()).toString();
	}
}
