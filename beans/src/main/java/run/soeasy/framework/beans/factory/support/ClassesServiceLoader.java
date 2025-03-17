package run.soeasy.framework.beans.factory.support;

import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.collections.ServiceLoader;
import run.soeasy.framework.util.comparator.OrderComparator;

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
	public Iterator<S> iterator() {
		if (services == null) {
			synchronized (this) {
				if (services == null) {
					this.services = getList();
				}
			}
		}
		return services.iterator();
	}
}
