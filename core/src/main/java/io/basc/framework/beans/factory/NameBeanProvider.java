package io.basc.framework.beans.factory;

import java.util.Optional;

import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class NameBeanProvider<T> implements BeanProvider<T> {
	private final Elements<String> names;
	private final BeanFactory beanFactory;
	private volatile Elements<T> services;

	@Override
	public void reload() {
		synchronized (this) {
			services = null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Elements<T> getServices() {
		if (services == null) {
			synchronized (this) {
				if (services == null) {
					services = names.map((name) -> beanFactory.getBean(name)).map((e) -> (T) e);
				}
			}
		}
		return services;
	}

	@Override
	public boolean isUnique() {
		return names.isUnique();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<T> getUnique() {
		if (isUnique()) {
			T bean = (T) beanFactory.getBean(names.first());
			return Optional.of(bean);
		}
		return Optional.empty();
	}

	@Override
	public boolean isEmpty() {
		return names.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public Optional<T> findFirst() {
		return names.findFirst().map((e) -> (T) beanFactory.getBean(e));
	}
}
