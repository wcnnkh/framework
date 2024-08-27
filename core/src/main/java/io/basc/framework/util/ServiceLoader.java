package io.basc.framework.util;

import java.util.function.Function;
import java.util.stream.Stream;

public interface ServiceLoader<S> extends Reloadable, Elements<S> {
	static final ServiceLoader<?> EMPTY_SERVICE_LOADER = new EmptyServiceLoader<>();

	@SuppressWarnings("unchecked")
	public static <E> ServiceLoader<E> empty() {
		return (ServiceLoader<E>) EMPTY_SERVICE_LOADER;
	}

	/**
	 * @param <T>
	 * @param iterable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> ServiceLoader<T> of(Iterable<? extends T> iterable) {
		if (iterable == null) {
			return empty();
		}

		if (iterable instanceof ServiceLoader) {
			return (ServiceLoader<T>) iterable;
		}

		return new StaticServiceLoader<>(iterable);
	}

	@Override
	default ServiceLoader<S> concat(Elements<? extends S> elements) {
		ServiceLoader<S> serviceLoader = ServiceLoader.of(elements.map((e) -> e));
		return ServiceLoader.this.concat(serviceLoader);
	}

	default ServiceLoader<S> concat(ServiceLoader<? extends S> serviceLoader) {
		return new MergedServiceLoader<>(Elements.forArray(this, serviceLoader));
	}

	@Override
	default <U> ServiceLoader<U> convert(Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return new ConvertibleServiceLoader<>(this, (elements) -> elements.convert(converter));
	}

	@Override
	default Stream<S> stream() {
		return Streams.stream(iterator());
	}
}
