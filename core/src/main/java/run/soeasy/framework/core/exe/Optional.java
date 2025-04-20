package run.soeasy.framework.core.exe;

import java.io.Serializable;
import java.util.NoSuchElementException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@FunctionalInterface
public interface Optional<T, E extends Throwable> extends Supplier<T, E> {
	public static class EmptyOptional<T, E extends Throwable> implements Optional<T, E> {

		public Optional<T, E> filter(@NonNull Predicate<? super T, ? extends E> filter) {
			return this;
		}

		@Override
		public <R, X extends Throwable> R apply(@NonNull Function<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			return mapper.apply(null);
		}
	}

	public static class MappedOptional<S, T, E extends Throwable, W extends Optional<S, E>>
			extends MappedSupplier<S, T, E, W> implements Optional<T, E> {
		public MappedOptional(@NonNull W source, @NonNull Function<? super S, ? extends T, ? extends E> mapper) {
			super(source, mapper);
		}

		@Override
		public T orElse(T other) throws E {
			S source = this.source.orElse(null);
			return mapper.apply(source);
		}

		@Override
		public <R> Optional<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> mapper) {
			return Optional.super.map(mapper);
		}

		@Override
		public <R, X extends Throwable> R apply(@NonNull Function<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			S source = this.source.orElse(null);
			T target = this.mapper.apply(source);
			return mapper.apply(target);
		}
	}

	@FunctionalInterface
	public static interface OptionalWrapper<T, E extends Throwable, W extends Optional<T, E>>
			extends Optional<T, E>, SupplierWrapper<T, E, W> {
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

		@Override
		default java.util.Optional<T> apply() throws E {
			return getSource().apply();
		}

		@Override
		default <R, X extends Throwable> R apply(@NonNull Function<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			return getSource().apply(mapper);
		}
	}

	@RequiredArgsConstructor
	public static class SharedOptional<T, E extends Throwable> implements Optional<T, E>, Serializable {
		private static final long serialVersionUID = 1L;
		protected final T target;

		@Override
		public <R, X extends Throwable> R apply(@NonNull Function<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			return mapper.apply(target);
		}
	}

	@RequiredArgsConstructor
	public static class SourceOptional<T, E extends Throwable, W extends Supplier<? extends T, ? extends E>>
			implements Optional<T, E> {
		@NonNull
		protected final W source;

		@Override
		public <R, X extends Throwable> R apply(@NonNull Function<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			return mapper.apply(source.get());
		}
	}

	public static final EmptyOptional<?, ?> EMPTY_OPTIONAL = new EmptyOptional<>();

	@SuppressWarnings("unchecked")
	public static <U, E extends Throwable> Optional<U, E> empty() {
		return (Optional<U, E>) EMPTY_OPTIONAL;
	}

	public static <U, E extends Throwable> Optional<U, E> ofNullable(U value) {
		if (value == null) {
			return empty();
		}
		return new SharedOptional<>(value);
	}

	public static <U, E extends Throwable> Optional<U, E> of(@NonNull Supplier<? extends U, ? extends E> source) {
		return new SourceOptional<>(source);
	}

	default Optional<T, E> filter(@NonNull Predicate<? super T, ? extends E> filter) {
		return map((e) -> filter.test(e) ? null : e);
	}

	@Override
	default T get() throws E, NoSuchElementException {
		T value = orElse(null);
		if (value == null) {
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	<R, X extends Throwable> R apply(@NonNull Function<? super T, ? extends R, ? extends X> mapper) throws E, X;

	default <X extends Throwable> void ifPresent(Consumer<? super T, ? extends X> consumer) throws E, X {
		apply((e) -> {
			if (e == null) {
				return e;
			}
			consumer.accept(e);
			return e;
		});
	}

	default boolean isPresent() throws E {
		return orElse(null) != null;
	}

	@Override
	default <R> Optional<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> mapper) {
		return new MappedOptional<>(this, mapper);
	}

	default T orElse(T other) throws E {
		return apply((e) -> e == null ? other : e);
	}

	default <X extends Throwable> T orElseGet(@NonNull Supplier<? extends T, ? extends X> suppler) throws E, X {
		return apply((e) -> e == null ? suppler.get() : e);
	}

	default <X extends Throwable> T orElseThrow(Supplier<? extends X, ? extends X> exceptionSupplier) throws E, X {
		return apply((e) -> {
			if (e == null) {
				throw exceptionSupplier.get();
			}
			return e;
		});
	}

	/**
	 * 应用
	 * 
	 * @see java.util.Optional
	 * @return
	 * @throws E
	 */
	default java.util.Optional<T> apply() throws E {
		return apply(java.util.Optional::ofNullable);
	}
}
