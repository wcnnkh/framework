package io.basc.framework.util;

import java.util.function.Function;

public interface ServiceLoader<S> {
	@SuppressWarnings("unchecked")
	public static <T> ServiceLoader<T> empty() {
		return (ServiceLoader<T>) EmptyServiceLoader.EMPTY;
	}

	public static <T> ServiceLoader<T> of(Iterable<T> elements) {
		if (elements == null) {
			return empty();
		}

		Elements<T> services = Elements.of(elements);
		return new SharedServiceLoader<>(services);
	}

	default <U> ServiceLoader<U> convert(Function<? super Elements<S>, ? extends Elements<U>> converter) {
		return new ConvertibleServiceLoader<>(this, converter);
	}

	void reload();

	Elements<S> getServices();
}
