package io.basc.framework.beans.factory.support;

import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.beans.factory.ListableBeanFactory;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.comparator.OrderComparator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ListableServiceLoader<S> implements ServiceLoader<S> {
	private final ListableBeanFactory listableBeanFactory;
	private final Class<S> serviceClass;
	private volatile List<S> services;

	private List<S> getList() {
		return listableBeanFactory.getBeansOfType(serviceClass).values().stream().sorted(OrderComparator.INSTANCE)
				.collect(Collectors.toList());
	}

	@Override
	public void reload() {
		if (services != null) {
			synchronized (this) {
				if (services != null) {
					this.services = getList();
				}
			}
		}
	}

	@Override
	public Elements<S> getServices() {
		if (services == null) {
			synchronized (this) {
				if (services == null) {
					this.services = getList();
				}
			}
		}
		return Elements.of(services);
	}

}
