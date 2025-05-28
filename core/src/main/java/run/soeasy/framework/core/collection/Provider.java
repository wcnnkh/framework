package run.soeasy.framework.core.collection;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import lombok.NonNull;

public interface Provider<S> extends Elements<S>, Reloadable {

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

	@Override
	default Provider<S> filter(@NonNull Predicate<? super S> predicate) {
		return convert(true, (e) -> e.filter(predicate));
	}

	@Override
	default <U> Provider<U> map(Function<? super S, ? extends U> mapper) {
		return convert(false, (e) -> e.map(mapper));
	}

	@Override
	default Provider<S> knownSize(@NonNull ToLongFunction<? super Elements<S>> statisticsSize) {
		return new KnownSizeProvider<>(this, statisticsSize);
	}

	@Override
	default Stream<S> stream() {
		return CollectionUtils.unknownSizeStream(iterator());
	}
}
