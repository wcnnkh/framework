package io.basc.framework.beans.factory.support;

import java.util.List;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.OrderComparator;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ServiceLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ClassesServiceLoader<S> implements ServiceLoader<S> {
	private final BeanFactory beanFactory;
	private final Elements<? extends Class<? extends S>> classes;
	private volatile List<S> services;

	private List<S> getList() {
		return classes.map((clazz) -> beanFactory.getBean(clazz)).sorted(OrderComparator.INSTANCE).toList();
	}

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
