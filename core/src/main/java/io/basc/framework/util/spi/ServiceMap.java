package io.basc.framework.util.spi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import io.basc.framework.util.Elements;
import io.basc.framework.util.MultiValueMap;
import io.basc.framework.util.Registration;
import io.basc.framework.util.comparator.TypeComparator;
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
		Services<S> services = container.get(requiredType);
		if (services == null) {
			services = servicesCreator.apply(requiredType);
			container.put(requiredType, services);
		}
		return services.register(service);
	}

	public Registration register(Class<?> requiredType, S service, int order) {
		Services<S> services = container.get(requiredType);
		if (services == null) {
			services = servicesCreator.apply(requiredType);
			container.put(requiredType, services);
		}
		return services.register(order, service);
	}

	public Elements<S> match(Class<?> requiredType) {
		// TODO
		return Elements.empty();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<S> get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<S> put(Class<?> key, List<S> value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<S> remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends Class<?>, ? extends List<S>> m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<Class<?>> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<List<S>> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<Class<?>, List<S>>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public S getFirst(Class<?> key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Class<?> key, S value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void set(Class<?> key, S value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAll(Map<Class<?>, S> values) {
		// TODO Auto-generated method stub

	}
}
