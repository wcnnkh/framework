package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.Reloadable;
import run.soeasy.framework.core.math.NumberValue;

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
		default <U> Provider<U> convert(boolean resize, Function<? super Stream<S>, ? extends Stream<U>> converter) {
			return getSource().convert(resize, converter);
		}

		@Override
		default Provider<S> concat(Elements<? extends S> elements) {
			return getSource().concat(elements);
		}

		@Override
		default Provider<S> concat(Provider<? extends S> serviceLoader) {
			return getSource().concat(serviceLoader);
		}

		@Override
		default Provider<S> knownSize(@NonNull Function<? super Elements<S>, ? extends NumberValue> statisticsSize) {
			return getSource().knownSize(statisticsSize);
		}

		@Override
		default Provider<S> filter(@NonNull Predicate<? super S> predicate) {
			return getSource().filter(predicate);
		}

		@Override
		default <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
			return getSource().map(mapper);
		}
	}

	@RequiredArgsConstructor
	public static class IterableProvider<S> implements ReloadableElementsWrapper<S, Elements<S>> {
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
		private static final Provider<?> EMPTY_PROVIDER = new EmptyProvider<>();

		public void reload() {
		}

		@Override
		public Provider<S> filter(Predicate<? super S> predicate) {
			return this;
		}

		@Override
		public <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
			return empty();
		}
	}

	public static class ConvertedProvider<S, T, W extends Provider<S>> extends ConvertedElements<S, T, W>
			implements Provider<T> {

		public ConvertedProvider(@NonNull W target, boolean resize,
				@NonNull Function<? super Stream<S>, ? extends Stream<T>> converter) {
			super(target, resize, converter);
		}

		@Override
		public void reload() {
			getTarget().reload();
		}

		@Override
		public <U> Provider<U> convert(boolean resize, Function<? super Stream<T>, ? extends Stream<U>> converter) {
			return Provider.super.convert(resize, converter);
		}

		@Override
		public Provider<T> concat(Elements<? extends T> elements) {
			return Provider.super.concat(elements);
		}

		@Override
		public Stream<T> stream() {
			return getSource().stream();
		}

		@Override
		public Provider<T> knownSize(@NonNull Function<? super Elements<T>, ? extends NumberValue> statisticsSize) {
			return Provider.super.knownSize(statisticsSize);
		}

		@Override
		public Provider<T> filter(@NonNull Predicate<? super T> predicate) {
			return Provider.super.filter(predicate);
		}

		@Override
		public <U> Provider<U> map(Function<? super T, ? extends U> mapper) {
			return Provider.super.map(mapper);
		}
	}

	public static interface ReloadableElementsWrapper<S, W extends Elements<S>>
			extends Provider<S>, ElementsWrapper<S, W> {

		@Override
		default <U> Provider<U> convert(boolean resize, Function<? super Stream<S>, ? extends Stream<U>> converter) {
			return Provider.super.convert(resize, converter);
		}

		@Override
		default Provider<S> concat(Elements<? extends S> elements) {
			return Provider.super.concat(elements);
		}

		@Override
		default Stream<S> stream() {
			return ElementsWrapper.super.stream();
		}

		@Override
		default Provider<S> knownSize(@NonNull Function<? super Elements<S>, ? extends NumberValue> statisticsSize) {
			return Provider.super.knownSize(statisticsSize);
		}

		@Override
		default Provider<S> filter(@NonNull Predicate<? super S> predicate) {
			return Provider.super.filter(predicate);
		}

		@Override
		default <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
			return Provider.super.map(mapper);
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

	@SuppressWarnings("unchecked")
	public static <E> Provider<E> empty() {
		return (Provider<E>) EmptyProvider.EMPTY_PROVIDER;
	}

	/**
	 * @param <T>
	 * @param iterable
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Provider<T> forIterable(@NonNull Iterable<? extends T> iterable) {
		if (iterable instanceof Provider) {
			return (Provider<T>) iterable;
		}

		return new IterableProvider<>(iterable);
	}

	public static <T> Provider<T> forSupplier(@NonNull Supplier<? extends T> supplier) {
		return forIterable(Elements.forSupplier(supplier));
	}

	@Override
	default Provider<S> concat(Elements<? extends S> elements) {
		Provider<S> serviceLoader = Provider.forIterable(elements.map((e) -> e));
		return Provider.this.concat(serviceLoader);
	}

	default Provider<S> concat(Provider<? extends S> serviceLoader) {
		return new MergedProvider<>(Elements.forArray(this, serviceLoader));
	}

	@Override
	default <U> Provider<U> convert(boolean resize, Function<? super Stream<S>, ? extends Stream<U>> converter) {
		return new ConvertedProvider<>(this, resize, converter);
	}

	public static class KnownSizeProvider<S, W extends Provider<S>> extends KnownSizeElements<S, W>
			implements ReloadableElementsWrapper<S, W> {

		public KnownSizeProvider(@NonNull W source,
				@NonNull Function<? super W, ? extends NumberValue> statisticsSize) {
			super(source, statisticsSize);
		}

		@Override
		public void reload() {
			getSource().reload();
		}
	}

	@Override
	default Provider<S> filter(@NonNull Predicate<? super S> predicate) {
		return convert(true, (e) -> e.filter(predicate));
	}

	@Override
	default <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
		return convert(false, (e) -> e.map(mapper));
	}

	@Override
	default Provider<S> knownSize(@NonNull Function<? super Elements<S>, ? extends NumberValue> statisticsSize) {
		return new KnownSizeProvider<>(this, statisticsSize);
	}

	@Override
	default Stream<S> stream() {
		return Streams.stream(iterator());
	}
}
