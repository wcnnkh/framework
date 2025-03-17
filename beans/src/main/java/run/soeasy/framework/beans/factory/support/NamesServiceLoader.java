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
