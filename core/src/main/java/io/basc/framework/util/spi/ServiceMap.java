package io.basc.framework.util.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.comparator.TypeComparator;
import io.basc.framework.util.concurrent.AtomicEntry;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.register.container.EntryRegistration;
import io.basc.framework.util.register.container.TreeMapContainer;
import lombok.NonNull;

public class ServiceMap<S> implements MultiValueMap<Class<?>, S> {
	private final TreeMapContainer<Class<?>, Services<S>> container = new TreeMapContainer<>();
	@NonNull
	private final Function<? super Class<?>, ? extends Services<S>> servicesCreator;

	public ServiceMap() {
		this((key) -> new Services<>());
	}

	public ServiceMap(@NonNull Function<? super Class<?>, ? extends Services<S>> servicesCreator) {
		this.container.setComparator(TypeComparator.DEFAULT);
		this.servicesCreator = servicesCreator;
	}

	public Registration register(Class<?> requiredType, S service) {
		Lock lock = container.writeLock();
		lock.lock();
		try {
			Services<S> services = container.get(requiredType);
			if (services == null) {
				services = servicesCreator.apply(requiredType);
				container.put(requiredType, services);
			}
			return services.register(service);
		} finally {
			lock.unlock();
		}
	}

	public Registration register(Class<?> requiredType, S service, int order) {
		Lock lock = container.writeLock();
		lock.lock();
		try {
			Services<S> services = container.get(requiredType);
			if (services == null) {
				services = servicesCreator.apply(requiredType);
				container.put(requiredType, services);
			}
			return services.register(order, service);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 搜索对应的服务列表
	 * 
	 * @param requiredType
	 * @return
	 */
	public Elements<S> search(Class<?> requiredType) {
		return container.readAsElements((map) -> {
			if (map == null) {
				return Elements.empty();
			}

			EntryRegistration<Class<?>, Services<S>> registration = map.get(requiredType);
			if (registration != null) {
				return registration.getValue();
			}

			return Elements.of(() -> map.entrySet().stream().filter((e) -> requiredType.isAssignableFrom(e.getKey()))
					.flatMap((e) -> e.getValue().getPayload().getValue().stream()));
		});
	}

	@Override
	public int size() {
		return container.size();
	}

	@Override
	public boolean isEmpty() {
		return container.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		Services<S> services = container.get(key);
		if (services == null) {
			return false;
		}

		return !services.isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		if (container.containsValue(value)) {
			return true;
		}

		for (Entry<Class<?>, Services<S>> entry : container.entrySet()) {
			if (entry.getValue().contains(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<S> get(Object key) {
		return container.readAsList((map) -> {
			EntryRegistration<Class<?>, Services<S>> registration = map.get(key);
			if (registration == null) {
				return null;
			}
			return registration.getValue();
		});
	}

	@Override
	public List<S> put(Class<?> key, List<S> value) {
		Lock lock = container.writeLock();
		lock.lock();
		try {
			Services<S> services = container.get(key);
			if (services == null) {
				services = servicesCreator.apply(key);
				services.registers(Elements.of(value));
				container.put(key, services);
				return null;
			} else {
				List<S> oldList = services.toList();
				services.clear();
				services.registers(Elements.of(value));
				return oldList;
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public List<S> remove(Object key) {
		Lock lock = container.writeLock();
		lock.lock();
		try {
			Services<S> services = container.remove(key);
			if (services == null) {
				return null;
			}

			List<S> oldList = services.toList();
			services.clear();
			return oldList;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void putAll(Map<? extends Class<?>, ? extends List<S>> m) {
		for (Entry<? extends Class<?>, ? extends List<S>> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		Lock lock = container.writeLock();
		lock.lock();
		try {
			for (Entry<Class<?>, Services<S>> entry : container.entrySet()) {
				entry.getValue().clear();
			}
			container.clear();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Set<Class<?>> keySet() {
		return container.keySet();
	}

	@Override
	public Collection<List<S>> values() {
		return container.values().stream().map((e) -> e.toList()).collect(Collectors.toList());
	}

	@Override
	public Set<Entry<Class<?>, List<S>>> entrySet() {
		return container.entrySet().stream().map((e) -> {
			Entry<Class<?>, List<S>> entry = new AtomicEntry<>(e.getKey());
			entry.setValue(e.getValue().toList());
			return entry;
		}).collect(Collectors.toSet());
	}

	@Override
	public S getFirst(Class<?> key) {
		Services<S> services = container.get(key);
		if (services == null) {
			return null;
		}
		return services.first();
	}

	@Override
	public void adds(Class<?> key, List<S> values) {
		Lock lock = container.writeLock();
		lock.lock();
		try {
			Services<S> services = container.get(key);
			if (services == null) {
				services = servicesCreator.apply(key);
				container.put(key, services);
			}
			services.registers(Elements.of(values));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void set(Class<?> key, S value) {
		Lock lock = container.writeLock();
		lock.lock();
		try {
			Services<S> services = container.get(key);
			if (services == null) {
				services = servicesCreator.apply(key);
				container.put(key, services);
			}
			services.clear();
			services.register(value);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String toString() {
		return container.toString();
	}
}
