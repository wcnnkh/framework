package run.soeasy.framework.util.collection;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Provider<S> extends Elements<S>, Reloadable {

	@FunctionalInterface
	public static interface ProviderWrapper<S, W extends Provider<S>> extends Provider<S>, ElementsWrapper<S, W> {
		@Override
		default void reload() {
			getSource().reload();
		}

		@Override
		default Stream<S> stream() {
			return getSource().stream();
		}

		@Override
		default <U> Provider<U> convert(Function<? super Stream<S>, ? extends Stream<U>> converter) {
			return getSource().convert(converter);
		}

		@Override
		default Provider<S> concat(Elements<? extends S> elements) {
			return getSource().concat(elements);
		}

		@Override
		default Provider<S> concat(Provider<? extends S> serviceLoader) {
			return getSource().concat(serviceLoader);
		}
	}

	@RequiredArgsConstructor
	public static class StaticProvider<S> implements ReloadableElementsWrapper<S, Elements<S>> {
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

	public static class EmptyProvider<S> extends EmptyElements<S> implements Provider<S> {
		private static final long serialVersionUID = 1L;

		public void reload() {
		}
	}

	public static class ConvertedProvider<S, T, W extends Provider<S>> extends ConvertedElements<S, T, W>
			implements Provider<T> {

		public ConvertedProvider(@NonNull W target,
				@NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
			super(target, converter);
		}

		@Override
		public void reload() {
			getTarget().reload();
		}

		@Override
		public <U> Provider<U> convert(Function<? super Stream<T>, ? extends Stream<U>> converter) {
			return Provider.super.convert(converter);
		}

		@Override
		public Provider<T> concat(Elements<? extends T> elements) {
			return Provider.super.concat(elements);
		}

		@Override
		public Stream<T> stream() {
			return getSource().stream();
		}
	}

	public static interface ReloadableElementsWrapper<S, W extends Elements<S>>
			extends Provider<S>, ElementsWrapper<S, W> {

		@Override
		default <U> Provider<U> convert(Function<? super Stream<S>, ? extends Stream<U>> converter) {
			return Provider.super.convert(converter);
		}

		@Override
		default Provider<S> concat(Elements<? extends S> elements) {
			return Provider.super.concat(elements);
		}

		@Override
		default Stream<S> stream() {
			return ElementsWrapper.super.stream();
		}
	}

	public static class MergedProvider<S, T extends Provider<? extends S>>
			implements ReloadableElementsWrapper<S, Elements<S>> {
		private final Elements<Provider<? extends S>> elements;

		public MergedProvider(Elements<Provider<? extends S>> elements) {
			this.elements = elements;
		}

		@Override
		public void reload() {
			elements.forEach(Provider::reload);
		}

		@Override
		public Elements<S> getSource() {
			return elements.flatMap((e) -> e.map(Function.identity()));
		}

		@Override
		public Provider<S> concat(Provider<? extends S> serviceLoader) {
			return new MergedProvider<>(this.elements.concat(Elements.singleton(serviceLoader)));
		}
	}

	static final Provider<?> EMPTY_PROVIDER = new EmptyProvider<>();

	@SuppressWarnings("unchecked")
	public static <E> Provider<E> empty() {
		return (Provider<E>) EMPTY_PROVIDER;
	}

	/**
	 * @param <T>
	 * @param iterable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Provider<T> of(Iterable<? extends T> iterable) {
		if (iterable == null) {
			return empty();
		}

		if (iterable instanceof Provider) {
			return (Provider<T>) iterable;
		}

		return new StaticProvider<>(iterable);
	}

	@Override
	default Provider<S> concat(Elements<? extends S> elements) {
		Provider<S> serviceLoader = Provider.of(elements.map((e) -> e));
		return Provider.this.concat(serviceLoader);
	}

	default Provider<S> concat(Provider<? extends S> serviceLoader) {
		return new MergedProvider<>(Elements.forArray(this, serviceLoader));
	}

	@Override
	default <U> Provider<U> convert(Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return new ConvertedProvider<>(this, converter);
	}

	@Override
	default Stream<S> stream() {
		return Streams.stream(iterator());
	}
}
