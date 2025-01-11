package io.basc.framework.beans.factory.support;

import java.util.Iterator;
import java.util.List;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.collections.ServiceLoader;
import io.basc.framework.util.comparator.OrderComparator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class NamesServiceLoader<S> implements ServiceLoader<S> {
	private final BeanFactory beanFactory;
	private final Class<S> requiredType;
	private final Elements<String> names;
	private volatile List<S> services;

	@Override
	public void reload() {
		if (services != null) {
			synchronized (this) {
				if (services != null) {
					services = getList();
				}
			}
		}
	}

	private List<S> getList() {
		return names.map((name) -> beanFactory.getBean(name, requiredType)).sorted(OrderComparator.INSTANCE).toList();
	}

	@Override
	public Iterator<S> iterator() {
		if (services == null) {
			synchronized (this) {
				if (services == null) {
					services = getList();
				}
			}
		}
		return services.iterator();
	}
}
