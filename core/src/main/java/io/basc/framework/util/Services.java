package io.basc.framework.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

/**
 * 一个动态注入的实现
 * 
 * @author wcnnkh
 *
 * @param <S>
 */
public class Services<S> implements ServiceLoader<S>, DynamicElementRegistry<S> {
	private final BroadcastEventDispatcher<ChangeEvent<Elements<S>>> elementEventDispatcher;
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ServiceInjectors<S> serviceInjectors = new ServiceInjectors<>();
	private final ServiceLoaders<S> serviceLoaders = new ServiceLoaders<>(this);
	private volatile TreeMap<WeightedElement<S>, Map<ServiceInjector<? super S>, Registration>> serviceMap;
	private volatile long version = 0;

	public Services() {
		this(OrderComparator.INSTANCE);
	}

	public Services(Comparator<? super S> comparator) {
		this(comparator, new StandardBroadcastEventDispatcher<>());
	}

	public Services(Comparator<? super S> comparator,
			BroadcastEventDispatcher<ChangeEvent<Elements<S>>> elementEventDispatcher) {
		Assert.requiredArgument(comparator != null, "comparator");
		Assert.requiredArgument(elementEventDispatcher != null, "elementEventDispatcher");
		this.serviceMap = CollectionUtils.newStrictTreeMap((o1, o2) -> {
			int order = Integer.compare(o1.getWeight(), o2.getWeight());
			if (order == 0) {
				return comparator.compare(o1.getElement(), o2.getElement());
			}
			return order;
		});
		this.elementEventDispatcher = elementEventDispatcher;
		serviceInjectors.getElementEventDispatcher().registerListener((event) -> {
			WriteLock writeLock = this.lock.writeLock();
			try {
				writeLock.lock();
				updateInjector(event);
			} finally {
				writeLock.unlock();
			}
		});
	}

	private void change(WeightedElement<S> element, ChangeType changeType) {
		WriteLock writeLock = this.lock.writeLock();
		try {
			writeLock.lock();
			if (changeType == ChangeType.DELETE) {
				Map<ServiceInjector<? super S>, Registration> map = serviceMap.remove(element);
				if (map == null) {
					return;
				}

				for (Entry<ServiceInjector<? super S>, Registration> entry : map.entrySet()) {
					entry.getValue().unregister();
				}
			} else if (changeType == ChangeType.CREATE) {
				Map<ServiceInjector<? super S>, Registration> map = serviceMap.get(element);
				if (map == null) {
					map = new LinkedHashMap<>();
				}

				for (ServiceInjector<? super S> injector : this.serviceInjectors.getElements()) {
					Registration registration = injector.inject(element.getElement());
					map.put(injector, registration);
				}
				serviceMap.put(element, map);
			}
			elementEventDispatcher
					.publishEvent(new ChangeEvent<>(changeType, Elements.singleton(element.getElement())));
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Registrations<ElementRegistration<S>> clear() throws RegistrationException {
		WriteLock writeLock = this.lock.writeLock();
		try {
			writeLock.lock();
			Registration registration = serviceLoaders.clear();
			List<ElementRegistration<S>> list = new ArrayList<>(serviceMap.size());
			for (Entry<WeightedElement<S>, Map<ServiceInjector<? super S>, Registration>> entry : serviceMap
					.entrySet()) {
				if (entry.getValue() != null) {
					for (Entry<ServiceInjector<? super S>, Registration> en : entry.getValue().entrySet()) {
						if (en.getValue() == null) {
							continue;
						}

						en.getValue().unregister();
					}
				}
				list.add(new ElementRegistration<S>(entry.getKey().getElement(),
						() -> change(entry.getKey(), ChangeType.CREATE)).version(() -> this.version));
			}
			serviceMap.clear();
			elementEventDispatcher.publishEvent(new ChangeEvent<>(ChangeType.DELETE,
					Elements.of(() -> list.stream().map((e) -> e.getElement()).iterator())));
			return new Registrations<>(Elements.of(list)).and(registration);
		} finally {
			writeLock.unlock();
		}
	}

	public BroadcastEventDispatcher<ChangeEvent<Elements<S>>> getElementEventDispatcher() {
		return elementEventDispatcher;
	}

	@Override
	public BroadcastEventRegistry<ChangeEvent<Elements<S>>> getElementEventRegistry() {
		return elementEventDispatcher;
	}

	@Override
	public Elements<S> getElements() {
		serviceLoaders.touch();
		ReadLock readLock = this.lock.readLock();
		List<S> list;
		try {
			readLock.lock();
			// 通过拷贝的方式实现线程安全
			list = serviceMap.keySet().stream().map((e) -> e.getElement()).collect(Collectors.toList());
		} finally {
			readLock.unlock();
		}
		return Elements.of(list);
	}

	public final ReentrantReadWriteLock getLock() {
		return lock;
	}

	public ServiceInjectors<S> getServiceInjectors() {
		return serviceInjectors;
	}

	public ServiceLoaders<S> getServiceLoaders() {
		return serviceLoaders;
	}

	@Override
	public Elements<S> getServices() {
		return getElements();
	}

	@Override
	public final Registration register(S element) throws RegistrationException {
		return register(element, Ordered.DEFAULT_PRECEDENCE);
	}

	public Registration register(S element, int weight) throws RegistrationException {
		Assert.requiredArgument(element != null, "element");
		WriteLock writeLock = this.lock.writeLock();
		WeightedElement<S> weightedElement = new WeightedElement<>(weight, element);
		try {
			writeLock.lock();
			if (serviceMap.containsKey(weightedElement)) {
				return ElementRegistration.empty();
			}

			change(weightedElement, ChangeType.CREATE);
		} finally {
			writeLock.unlock();
		}

		// 需要判断是还是动态的，如果是要关联一下
		return new ElementRegistration<S>(element, () -> change(weightedElement, ChangeType.DELETE))
				.version(() -> this.version);
	}

	public Registration registerFirst(S element) throws RegistrationException {
		return register(element, Ordered.HIGHEST_PRECEDENCE);
	}

	public Registration registerLast(S element) throws RegistrationException {
		return register(element, Ordered.LOWEST_PRECEDENCE);
	}

	@Override
	public final Registrations<ElementRegistration<S>> registers(Iterable<? extends S> elements)
			throws RegistrationException {
		return DynamicElementRegistry.super.registers(elements);
	}

	@Override
	public void reload() {
		serviceLoaders.reload();
	}

	private void updateInjector(ChangeEvent<Elements<ServiceInjector<? super S>>> event) {
		List<S> update = new ArrayList<>();
		if (event.getChangeType() == ChangeType.CREATE) {
			for (ServiceInjector<? super S> injector : event.getSource()) {
				for (Entry<WeightedElement<S>, Map<ServiceInjector<? super S>, Registration>> entry : serviceMap
						.entrySet()) {
					if (entry.getValue() == null) {
						entry.setValue(new HashMap<>());
					}

					Registration registration = entry.getValue().get(injector);
					if (registration != null) {
						// TODO 一般来说不会到这里, 如果有可能在在bug, 因为这是新增
						registration.unregister();
					}

					registration = injector.inject(entry.getKey().getElement());
					entry.getValue().put(injector, registration);
					update.add(entry.getKey().getElement());
				}
			}
		} else if (event.getChangeType() == ChangeType.UPDATE) {
			for (ServiceInjector<? super S> injector : event.getSource()) {
				for (Entry<WeightedElement<S>, Map<ServiceInjector<? super S>, Registration>> entry : serviceMap
						.entrySet()) {
					if (entry.getValue() == null) {
						// TODO 都已经有injector了，怎么可能为空？
						entry.setValue(new HashMap<>());
					}

					Registration registration = entry.getValue().get(injector);
					if (registration == null) {
						// TODO 这是更新不可能到这里
						registration = injector.inject(entry.getKey().getElement());
					} else {
						registration.unregister();
						registration = injector.inject(entry.getKey().getElement());
					}
					entry.getValue().put(injector, registration);
					update.add(entry.getKey().getElement());
				}
			}
		} else if (event.getChangeType() == ChangeType.DELETE) {
			for (ServiceInjector<? super S> injector : event.getSource()) {
				for (Entry<WeightedElement<S>, Map<ServiceInjector<? super S>, Registration>> entry : serviceMap
						.entrySet()) {
					if (entry.getValue() == null) {
						continue;
					}

					Registration registration = entry.getValue().remove(injector);
					if (registration == null) {
						// TODO 为什么不存在了？
						continue;
					}

					registration.unregister();
					update.add(entry.getKey().getElement());
				}
			}
			elementEventDispatcher.publishEvent(new ChangeEvent<>(ChangeType.UPDATE, Elements.of(update)));
		}
	}

	@Override
	public String toString() {
		return serviceMap.keySet().toString();
	}
}
