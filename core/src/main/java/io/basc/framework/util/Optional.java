package io.basc.framework.util;

import java.io.Serializable;
import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@FunctionalInterface
public interface Optional<T, E extends Throwable> extends Source<T, E> {
	public static class EmptyOptional<T, E extends Throwable> implements Optional<T, E> {

		@Override
		public Optional<T, E> filter(@NonNull Filter<? super T, ? extends E> filter) {
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
		protected final Filter<? super T, ? extends E> filter;

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
		protected final Pipeline<? super S, ? extends T, ? extends E> pipeline;

		@Override
		public T orElse(T other) throws E {
			S source = this.source.orElse(null);
			if (source == null) {
				return other;
			}
			return pipeline.apply(source);
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
	public static class SourceOptional<T, E extends Throwable, W extends Source<? extends T, ? extends E>>
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

	public static <U, E extends Throwable> Optional<U, E> ofSource(@NonNull Source<? extends U, ? extends E> source) {
		return new SourceOptional<>(source);
	}

	default Optional<T, E> filter(@NonNull Filter<? super T, ? extends E> filter) {
		return new FilteredOptional<>(this, filter);
	}

	@Override
	default T get() throws E, NoSuchElementException {
		T value = orElse(null);
		if (value == null) {
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	default <X extends Throwable> void ifPresent(Endpoint<? super T, ? extends X> endpoint) throws E, X {
		T value = orElse(null);
		if (value != null) {
			endpoint.accept(value);
		}
	}

	default boolean isPresent() throws E {
		return orElse(null) != null;
	}

	@Override
	default <R> Optional<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedOptional<>(this, pipeline);
	}

	T orElse(T other) throws E;
}
