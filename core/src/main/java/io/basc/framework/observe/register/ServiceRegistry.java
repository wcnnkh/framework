package io.basc.framework.observe.register;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;

import io.basc.framework.core.OrderComparator;
import io.basc.framework.core.Ordered;
import io.basc.framework.observe.ChangeType;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Registration;
import io.basc.framework.util.RegistrationException;
import io.basc.framework.util.Registrations;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;
import io.basc.framework.util.select.WeightedElement;
import lombok.NonNull;

/**
 * 一个动态注入的实现
 * 
 * @author wcnnkh
 *
 * @param <S>
 */
public class ServiceRegistry<S> extends AbstractElementRegistry<S> {
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final ServiceInjectors<S> serviceInjectors = new ServiceInjectors<>();
	private final ServiceLoaderRegistry<S> serviceLoaderRegistry = new ServiceLoaderRegistry<>(this);
	private volatile TreeMap<WeightedElement<S>, Map<ServiceInjector<? super S>, Registration>> serviceMap;
	private volatile long version = 0;
	@NonNull
	private Comparator<? super S> comparator;

	public ServiceRegistry() {
		setServiceComparator(OrderComparator.INSTANCE);
		serviceInjectors.registerBatchListener((events) -> {
			WriteLock writeLock = this.lock.writeLock();
			try {
				writeLock.lock();
				updateInjector(events);
			} finally {
				writeLock.unlock();
			}
		});
	}

	private void initMap() {
		if (serviceMap == null) {
			this.serviceMap = newMap();
		}
	}

	private TreeMap<WeightedElement<S>, Map<ServiceInjector<? super S>, Registration>> newMap() {
		return CollectionUtils.newStrictTreeMap((o1, o2) -> {
			int order = Integer.compare(o1.getWeight(), o2.getWeight());
			if (order == 0) {
				return comparator.compare(o1.getElement(), o2.getElement());
			}
			return order;
		});
	}

	public void setServiceComparator(Comparator<? super S> comparator) {
		Assert.requiredArgument(comparator != null, "comparator");
		Lock writeLock = lock.writeLock();
		writeLock.lock();
		try {
			this.comparator = comparator;
			if (serviceMap != null) {
				TreeMap<WeightedElement<S>, Map<ServiceInjector<? super S>, Registration>> serviceMap = newMap();
				serviceMap.putAll(serviceMap);
				this.serviceMap = serviceMap;
			}
		} finally {
			writeLock.unlock();
		}
	}

	private void change(WeightedElement<S> element, ChangeType eventType) {
		WriteLock writeLock = this.lock.writeLock();
		try {
			writeLock.lock();
			if (eventType == ChangeType.DELETE) {
				if (serviceMap == null) {
					return;
				}

				Map<ServiceInjector<? super S>, Registration> map = serviceMap.remove(element);
				if (map == null) {
					return;
				}

				for (Entry<ServiceInjector<? super S>, Registration> entry : map.entrySet()) {
					entry.getValue().unregister();
				}
			} else if (eventType == ChangeType.CREATE) {
				initMap();

				Map<ServiceInjector<? super S>, Registration> map = serviceMap.get(element);
				if (map == null) {
					map = new LinkedHashMap<>();
				}

				for (ServiceInjector<? super S> injector : this.serviceInjectors.getServices()) {
					Registration registration = injector.inject(element.getElement());
					map.put(injector, registration);
				}
				serviceMap.put(element, map);
			}
			publishEvent(new RegistryEvent<>(this, eventType, element.getElement()));
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public Registrations<ElementRegistration<S>> clear() throws RegistrationException {
		WriteLock writeLock = this.lock.writeLock();
		try {
			writeLock.lock();
			Registration registration = serviceLoaderRegistry.clear();
			List<ElementRegistration<S>> list = new ArrayList<>();
			if (serviceMap != null) {
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
				publishBatchEvent(
						Elements.of(list).map((e) -> new RegistryEvent<>(this, ChangeType.DELETE, e.getElement())));
			}
			return new Registrations<>(Elements.of(list)).and(registration);
		} finally {
			writeLock.unlock();
		}
	}

	public final ReentrantReadWriteLock getLock() {
		return lock;
	}

	public ServiceInjectors<S> getServiceInjectors() {
		return serviceInjectors;
	}

	public ServiceLoaderRegistry<S> getServiceLoaderRegistry() {
		return serviceLoaderRegistry;
	}

	@Override
	protected Elements<S> loadServices() {
		serviceLoaderRegistry.touch();
		return serviceMap == null ? Elements.empty()
				: Elements.of(serviceMap.keySet().stream().map((e) -> e.getElement()).collect(Collectors.toList()));
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
			initMap();
			if (serviceMap.containsKey(weightedElement)) {
				return ElementRegistration.empty();
			}

			change(weightedElement, ChangeType.CREATE);
		} finally {
			writeLock.unlock();
		}

		Registration registration = new ElementRegistration<S>(element,
				() -> change(weightedElement, ChangeType.DELETE)).version(() -> this.version);
		// 判断是否是动态的，如果是要关联一下
		if (element != this && element instanceof Registry) {
			registration = registration
					.and(((Registry<?>) element).registerListener((e) -> change(weightedElement, ChangeType.UPDATE)));
		}
		return registration;
	}

	public final Registration registerFirst(S element) throws RegistrationException {
		return register(element, Ordered.HIGHEST_PRECEDENCE);
	}

	public final Registration registerLast(S element) throws RegistrationException {
		return register(element, Ordered.LOWEST_PRECEDENCE);
	}

	public Registration registerServiceLoader(ServiceLoader<? extends S> serviceLoader) {
		return serviceLoaderRegistry.register(serviceLoader);
	}

	@Override
	public void reload() {
		serviceLoaderRegistry.reload();
	}

	@Override
	public String toString() {
		return getServices().toString();
	}

	private void updateInjector(Elements<RegistryEvent<ServiceInjector<? super S>>> events) {
		if (serviceMap == null) {
			return;
		}

		List<S> update = new ArrayList<>();
		for (RegistryEvent<ServiceInjector<? super S>> event : events) {
			ServiceInjector<? super S> injector = event.getPayload();
			if (event.getType() == ChangeType.CREATE) {
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
			} else if (event.getType() == ChangeType.UPDATE) {
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
			} else if (event.getType() == ChangeType.DELETE) {
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
		}
		publishBatchEvent(Elements.of(update).map((e) -> new RegistryEvent<>(this, ChangeType.UPDATE, e)).toList());
	}
}
