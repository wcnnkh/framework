package io.basc.framework.util.element;

import java.util.function.Function;

public interface ServiceLoader<S> {
	@SuppressWarnings("unchecked")
	public static <T> ServiceLoader<T> empty() {
		return (ServiceLoader<T>) EmptyServiceLoader.EMPTY;
	}

	public static <T> ServiceLoader<T> of(Elements<T> services) {
		if (services == null) {
			return empty();
		}

		return new FinalServiceLoader<>(services);
	}

	default <U> ServiceLoader<U> convert(Function<? super Elements<S>, ? extends Elements<U>> converter) {
		return new ConvertibleServiceLoader<>(this, converter);
	}

	void reload();

	Elements<S> getServices();

	@SuppressWarnings("unchecked")
	default ServiceLoader<S> concat(ServiceLoader<S> serviceLoader) {
		Elements<? extends ServiceLoader<S>> serviceLoaders = Elements.forArray(this, serviceLoader);
		return new MultiServiceLoader<>(serviceLoaders);
	}
}
