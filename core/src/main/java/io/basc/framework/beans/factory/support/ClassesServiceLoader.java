package io.basc.framework.beans.factory.support;

import java.util.List;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.OrderComparator;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ClassesServiceLoader<S> implements ServiceLoader<S> {
	private final BeanFactory beanFactory;
	private final Class<S> serviceClass;
	private final Elements<? extends Class<?>> classes;
	private volatile List<S> services;

	private List<S> getList() {
		Elements<Object> objects = classes.map((clazz) -> beanFactory.getBean(clazz));
		return objects.filter((bean) -> serviceClass.isInstance(bean)).map((bean) -> serviceClass.cast(bean))
				.sorted(OrderComparator.INSTANCE).toList();
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
