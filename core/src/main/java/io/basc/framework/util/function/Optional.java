package io.basc.framework.util.function;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@FunctionalInterface
public interface Optional<T, E extends Throwable> extends Supplier<T, E> {
	public static class EmptyOptional<T, E extends Throwable> implements Optional<T, E> {

		@Override
		public Optional<T, E> filter(@NonNull Predicate<? super T, ? extends E> filter) {
			return this;
		}

		@Override
		public T orElse(T other) throws E {
			return other;
		}
	}

	@RequiredArgsConstructor
	public static class FilteredOptional<T, E extends Throwable> implements Optional<T, E> {
		@NonNull
		protected final Optional<T, E> source;
		@NonNull
		protected final Predicate<? super T, ? extends E> filter;

		@Override
		public T orElse(T other) throws E {
			T source = this.source.orElse(null);
			if (source == null) {
				return other;
			}

			if (!filter.test(source)) {
				return other;
			}
			return source;
		}
	}

	@RequiredArgsConstructor
	public static class MappedOptional<S, T, E extends Throwable> implements Optional<T, E> {
		@NonNull
		protected final Optional<S, E> source;
		@NonNull
		protected final Function<? super S, ? extends T, ? extends E> pipeline;

		@Override
		public T orElse(T other) throws E {
			S source = this.source.orElse(null);
			if (source == null) {
				return other;
			}
			return pipeline.apply(source);
		}
	}

	@FunctionalInterface
	public static interface OptionalWrapper<T, E extends Throwable, W extends Optional<T, E>>
			extends Optional<T, E>, SourceWrapper<T, E, W> {
		@Override
		default Optional<T, E> filter(@NonNull Predicate<? super T, ? extends E> filter) {
			return getSource().filter(filter);
		}

		@Override
		default T get() throws E, NoSuchElementException {
			return getSource().get();
		}

		@Override
		default boolean isPresent() throws E {
			return getSource().isPresent();
		}

		@Override
		default <R> Optional<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
			return getSource().map(pipeline);
		}

		@Override
		default T orElse(T other) throws E {
			return getSource().orElse(other);
		}

		@Override
		default <X extends Throwable> T orElseGet(@NonNull Supplier<? extends T, ? extends X> source) throws E, X {
			return getSource().orElseGet(source);
		}
	}

	@RequiredArgsConstructor
	public static class SharedOptional<T, E extends Throwable> implements Optional<T, E>, Serializable {
		private static final long serialVersionUID = 1L;
		protected final T target;

		@Override
		public T orElse(T other) throws E {
			return target == null ? other : target;
		}
	}

	@RequiredArgsConstructor
	public static class SourceOptional<T, E extends Throwable, W extends Supplier<? extends T, ? extends E>>
			implements Optional<T, E> {
		@NonNull
		protected final W source;

		@Override
		public T orElse(T other) throws E {
			T value = source.get();
			return value == null ? other : value;
		}
	}

	public static final EmptyOptional<?, ?> EMPTY_OPTIONAL = new EmptyOptional<>();

	@SuppressWarnings("unchecked")
	public static <U, E extends Throwable> Optional<U, E> empty() {
		return (Optional<U, E>) EMPTY_OPTIONAL;
	}

	public static <U, E extends Throwable> Optional<U, E> of(U value) {
		if (value == null) {
			return empty();
		}
		return new SharedOptional<>(value);
	}

	public static <U, E extends Throwable> Optional<U, E> ofSource(@NonNull Supplier<? extends U, ? extends E> source) {
		return new SourceOptional<>(source);
	}

	default Optional<T, E> filter(@NonNull Predicate<? super T, ? extends E> filter) {
		return new FilteredOptional<>(this, filter);
	}

	default <U, X extends Throwable> Optional<U, X> flatMap(
			@NonNull Function<? super T, ? extends Optional<U, X>, ? extends E> mapper) throws E {
		T value = orElse(null);
		if (value == null) {
			return empty();
		} else {
			return Objects.requireNonNull(mapper.apply(value));
		}
	}

	@Override
	default T get() throws E, NoSuchElementException {
		T value = orElse(null);
		if (value == null) {
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	default <X extends Throwable> void ifPresent(Consumer<? super T, ? extends X> endpoint) throws E, X {
		T value = orElse(null);
		if (value != null) {
			endpoint.accept(value);
		}
	}

	default boolean isPresent() throws E {
		return orElse(null) != null;
	}

	@Override
	default <R> Optional<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedOptional<>(this, pipeline);
	}

	T orElse(T other) throws E;

	default <X extends Throwable> T orElseGet(@NonNull Supplier<? extends T, ? extends X> source) throws E, X {
		T target = orElse(null);
		return target == null ? source.get() : target;
	}
}
