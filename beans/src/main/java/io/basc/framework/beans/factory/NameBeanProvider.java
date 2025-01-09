package io.basc.framework.beans.factory;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Elements.ElementsWrapper;
import io.basc.framework.util.NoUniqueElementException;
import io.basc.framework.util.ServiceLoader;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class NameBeanProvider<T> implements ServiceLoader<T>, ElementsWrapper<T, Elements<T>> {
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
	public Elements<T> getSource() {
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
	public T getUnique() {
		if (isUnique()) {
			return (T) beanFactory.getBean(names.first());
		}
		throw new NoUniqueElementException();
	}

	@Override
	public boolean isEmpty() {
		return names.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<T> findFirst() {
		return names.findFirst().map((e) -> (T) beanFactory.getBean(e));
	}

	@Override
	public <U> ServiceLoader<U> convert(Function<? super Stream<T>, ? extends Stream<U>> converter) {
		return ServiceLoader.super.convert(converter);
	}

	@Override
	public ServiceLoader<T> concat(Elements<? extends T> elements) {
		return ServiceLoader.super.concat(elements);
	}

	@Override
	public Stream<T> stream() {
		return ServiceLoader.super.stream();
	}
}
