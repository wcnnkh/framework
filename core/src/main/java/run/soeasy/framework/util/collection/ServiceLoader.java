package run.soeasy.framework.util.collection;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface ServiceLoader<S> extends Elements<S>, Reloadable {

	@FunctionalInterface
	public static interface ServiceLoaderWrapper<S, W extends ServiceLoader<S>>
			extends ServiceLoader<S>, ElementsWrapper<S, W> {
		@Override
		default void reload() {
			getSource().reload();
		}

		@Override
		default Stream<S> stream() {
			return getSource().stream();
		}

		@Override
		default <U> ServiceLoader<U> convert(Function<? super Stream<S>, ? extends Stream<U>> converter) {
			return getSource().convert(converter);
		}

		@Override
		default ServiceLoader<S> concat(Elements<? extends S> elements) {
			return getSource().concat(elements);
		}

		@Override
		default ServiceLoader<S> concat(ServiceLoader<? extends S> serviceLoader) {
			return getSource().concat(serviceLoader);
		}
	}

	@RequiredArgsConstructor
	public static class StaticServiceLoader<S> implements ReloadableElementsWrapper<S, Elements<S>> {
		@NonNull
		private final Iterable<? extends S> source;

		@Override
		public Elements<S> getSource() {
			return Elements.of(source);
		}

		@Override
		public void reload() {
			if (source instanceof Reloadable) {
				((Reloadable) source).reload();
			}
		}
	}

	public static class EmptyServiceLoader<S> extends EmptyElements<S> implements ServiceLoader<S> {
		private static final long serialVersionUID = 1L;

		public void reload() {
		}
	}

	public static class ConvertedServiceLoader<S, T, W extends ServiceLoader<S>> extends ConvertedElements<S, T, W>
			implements ServiceLoader<T> {

		public ConvertedServiceLoader(@NonNull W target,
				@NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
			super(target, converter);
		}

		@Override
		public void reload() {
			getTarget().reload();
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
			return getSource().stream();
		}
	}

	public static interface ReloadableElementsWrapper<S, W extends Elements<S>>
			extends ServiceLoader<S>, ElementsWrapper<S, W> {

		@Override
		default <U> ServiceLoader<U> convert(Function<? super Stream<S>, ? extends Stream<U>> converter) {
			return ServiceLoader.super.convert(converter);
		}

		@Override
		default ServiceLoader<S> concat(Elements<? extends S> elements) {
			return ServiceLoader.super.concat(elements);
		}

		@Override
		default Stream<S> stream() {
			return ElementsWrapper.super.stream();
		}
	}

	public static class MergedServiceLoader<S, T extends ServiceLoader<? extends S>>
			implements ReloadableElementsWrapper<S, Elements<S>> {
		private final Elements<ServiceLoader<? extends S>> elements;

		public MergedServiceLoader(Elements<ServiceLoader<? extends S>> elements) {
			this.elements = elements;
		}

		@Override
		public void reload() {
			elements.forEach(ServiceLoader::reload);
		}

		@Override
		public Elements<S> getSource() {
			return elements.flatMap((e) -> e.map(Function.identity()));
		}

		@Override
		public ServiceLoader<S> concat(ServiceLoader<? extends S> serviceLoader) {
			return new MergedServiceLoader<>(this.elements.concat(Elements.singleton(serviceLoader)));
		}
	}

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
		return new ConvertedServiceLoader<>(this, converter);
	}

	@Override
	default Stream<S> stream() {
		return Streams.stream(iterator());
	}
}
