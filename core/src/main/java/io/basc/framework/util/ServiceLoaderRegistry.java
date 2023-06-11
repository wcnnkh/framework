package io.basc.framework.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;

import io.basc.framework.core.OrderComparator;
import io.basc.framework.core.Ordered;
import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.BroadcastEventRegistry;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.ChangeType;
import io.basc.framework.event.DynamicElementRegistry;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;

public class ServiceLoaderRegistry<S> implements ServiceLoader<S>, DynamicElementRegistry<ServiceLoader<? extends S>> {
	private volatile boolean changed;
	private final BroadcastEventDispatcher<ChangeEvent<Elements<ServiceLoader<? extends S>>>> elementEventDispatcher;
	/**
	 * 用来作为默认填充
	 */
	private final Registrations<ElementRegistration<S>> EMPTY = Registrations.empty();
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private volatile TreeMap<WeightedElement<ServiceLoader<? extends S>>, Registrations<ElementRegistration<S>>> map;
	private final ElementRegistry<S> registry;
	private int version;

	public ServiceLoaderRegistry() {
		this(new DefaultElementRegistry<>());
	}

	public ServiceLoaderRegistry(ElementRegistry<S> registry) {
		this(registry, OrderComparator.INSTANCE);
	}

	public ServiceLoaderRegistry(ElementRegistry<S> registry,
			Comparator<? super ServiceLoader<? extends S>> serviceLoaderComparator) {
		this(registry, serviceLoaderComparator, new StandardBroadcastEventDispatcher<>());
	}

	public ServiceLoaderRegistry(ElementRegistry<S> registry,
			Comparator<? super ServiceLoader<? extends S>> serviceLoaderComparator,
			BroadcastEventDispatcher<ChangeEvent<Elements<ServiceLoader<? extends S>>>> elementEventDispatcher) {
		Assert.requiredArgument(registry != null, "registry");
		Assert.requiredArgument(serviceLoaderComparator != null, "serviceLoaderComparator");
		Assert.requiredArgument(elementEventDispatcher != null, "elementEventDispatcher");
		this.map = CollectionUtils.newStrictTreeMap((o1, o2) -> {
			int order = Integer.compare(o1.getWeight(), o2.getWeight());
			if (order == 0) {
				return serviceLoaderComparator.compare(o1.getElement(), o2.getElement());
			}
			return order;
		});
		this.registry = registry;
		this.elementEventDispatcher = elementEventDispatcher;
	}

	@Override
	public Registrations<ElementRegistration<ServiceLoader<? extends S>>> clear() throws RegistrationException {
		WriteLock writeLock = lock.writeLock();
		try {
			writeLock.lock();
			this.version++;
			List<ElementRegistration<ServiceLoader<? extends S>>> registrations = new ArrayList<>(map.size());
			for (Entry<WeightedElement<ServiceLoader<? extends S>>, Registrations<ElementRegistration<S>>> entry : map
					.entrySet()) {
				entry.getValue().unregister();
				ElementRegistration<ServiceLoader<? extends S>> registration = new ElementRegistration<ServiceLoader<? extends S>>(
						entry.getKey().getElement(), () -> unregister(entry.getKey(), ChangeType.CREATE));
				registrations.add(registration.version(() -> this.version));
			}
			changed = true;
			return new Registrations<>(Elements.of(registrations));
		} finally {
			writeLock.unlock();
		}
	}

	public BroadcastEventDispatcher<ChangeEvent<Elements<ServiceLoader<? extends S>>>> getElementEventDispatcher() {
		return elementEventDispatcher;
	}

	@Override
	public BroadcastEventRegistry<ChangeEvent<Elements<ServiceLoader<? extends S>>>> getElementEventRegistry() {
		return elementEventDispatcher;
	}

	@Override
	public Elements<ServiceLoader<? extends S>> getElements() {
		ReadLock readLock = lock.readLock();
		try {
			readLock.lock();
			List<ServiceLoader<? extends S>> list = map.keySet().stream().map((e) -> e.getElement())
					.collect(Collectors.toList());
			return Elements.of(list);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public Elements<S> getServices() {
		// 获取迭代器并初始化ServiceLoader
		touch();
		ReadLock readLock = lock.readLock();
		try {
			readLock.lock();
			List<S> services = map.values().stream().flatMap((r) -> r.getElements().stream().map((e) -> e.getElement()))
					.collect(Collectors.toList());
			return Elements.of(services);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public final Registration register(ServiceLoader<? extends S> element) throws RegistrationException {
		return register(element, Ordered.DEFAULT_PRECEDENCE);
	}

	public Registration register(ServiceLoader<? extends S> element, int weight) {
		Assert.requiredArgument(element != null, "element");
		Assert.isTrue(element != registry && element != this, "There is a circular reference");

		WriteLock writeLock = lock.writeLock();
		WeightedElement<ServiceLoader<? extends S>> weightElement = new WeightedElement<ServiceLoader<? extends S>>(
				weight, element);
		try {
			writeLock.lock();
			if (element instanceof ServiceLoaderRegistry) {
				for (ServiceLoader<? extends S> serviceLoader : ((ServiceLoaderRegistry<? extends S>) element).getElements()) {
					Assert.isTrue(serviceLoader != registry && serviceLoader != this, "There is a circular reference");
				}
			}

			if (map.containsKey(weightElement)) {
				return Registration.EMPTY;
			}

			Registrations<ElementRegistration<S>> set = map.putIfAbsent(weightElement, EMPTY);
			if (set != null) {
				return Registration.EMPTY;
			}
			changed = true;

			// TODO 后面希望能做到懒加载
			touch();
		} finally {
			writeLock.unlock();
		}

		Registration registration = Registration.EMPTY;
		if (element instanceof DynamicElementRegistry) {
			registration = registration
					.and(((DynamicElementRegistry<?>) element).getElementEventRegistry().registerListener((event) -> {
						elementEventDispatcher
								.publishEvent(new ChangeEvent<>(ChangeType.UPDATE, Elements.singleton(element)));
						changed = true;
					}));
		}

		return new VersionRegistration(() -> this.version, () -> unregister(weightElement, ChangeType.DELETE))
				.and(registration);
	}

	public Registration registerFirst(ServiceLoader<? extends S> element) {
		return register(element, Ordered.HIGHEST_PRECEDENCE);
	}

	public Registration registerLast(ServiceLoader<? extends S> element) {
		return register(element, Ordered.LOWEST_PRECEDENCE);
	}

	@Override
	public final Registrations<ElementRegistration<ServiceLoader<? extends S>>> registers(
			Iterable<? extends ServiceLoader<? extends S>> elements) throws RegistrationException {
		Assert.requiredArgument(elements != null, "elements");
		return DynamicElementRegistry.super.registers(elements);
	}

	@Override
	public void reload() {
		for (Entry<WeightedElement<ServiceLoader<? extends S>>, Registrations<ElementRegistration<S>>> entry : map
				.entrySet()) {
			entry.getKey().getElement().reload();
			if (entry.getValue() == EMPTY) {
				// 还没有初始化过，可以忽略
				continue;
			}

			entry.getValue().unregister();
			// 再次调用iterator的时候会初始化
			entry.setValue(EMPTY);
		}
		changed = true;
	}

	/**
	 * 触发ServiceLoader初始化
	 */
	public void touch() {
		if (changed) {
			WriteLock writeLock = lock.writeLock();
			try {
				writeLock.lock();
				if (changed) {
					for (Entry<WeightedElement<ServiceLoader<? extends S>>, Registrations<ElementRegistration<S>>> entry : map
							.entrySet()) {
						// 因为此处校验，所以可以不用关心将changed=true时的线程安全性问题
						if (entry.getValue() != EMPTY) {
							// 已经初始化过了，可以忽略
							continue;
						}

						Registrations<ElementRegistration<S>> registration = registry
								.registers(entry.getKey().getElement().getServices());
						if (registration.isEmpty()) {
							// 初始化没拿到结果，忽略
							continue;
						}

						entry.setValue(registration);
					}
					changed = false;
				}
			} finally {
				writeLock.unlock();
			}
		}
	}

	private void unregister(WeightedElement<ServiceLoader<? extends S>> serviceLoader, ChangeType changeType) {
		WriteLock writeLock = lock.writeLock();
		try {
			writeLock.lock();
			if (changeType == ChangeType.CREATE) {
				map.putIfAbsent(serviceLoader, Registrations.empty());
			} else if (changeType == ChangeType.DELETE) {
				Registrations<ElementRegistration<S>> registrations = map.remove(serviceLoader);
				if (registrations != null && registrations != EMPTY) {
					registrations.unregister();
				}
			}
			changed = true;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
