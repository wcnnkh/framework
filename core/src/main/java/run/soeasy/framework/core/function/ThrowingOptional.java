package run.soeasy.framework.core.function;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface ThrowingOptional<T, E extends Throwable> extends ThrowingSupplier<T, E> {
	public static class EmptyThrowingOptional<T, E extends Throwable> implements ThrowingOptional<T, E> {
		private static final EmptyThrowingOptional<?, ?> INSTANCE = new EmptyThrowingOptional<>();

		public ThrowingOptional<T, E> filter(@NonNull ThrowingPredicate<? super T, ? extends E> filter) {
			return this;
		}

		@Override
		public <R, X extends Throwable> R apply(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			return mapper.apply(null);
		}
	}

	public static <U, E extends Throwable> ThrowingOptional<U, E> forSupplier(
			@NonNull ThrowingSupplier<? extends U, ? extends E> supplier) {
		return new ThrowingSupplierOptional<>(supplier);
	}

	@RequiredArgsConstructor
	public static class ValueThrowingOptional<T, E extends Throwable> implements ThrowingOptional<T, E>, Serializable {
		private static final long serialVersionUID = 1L;
		protected final T target;

		@Override
		public <R, X extends Throwable> R apply(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			return mapper.apply(target);
		}
	}

	public static <U, E extends Throwable> ThrowingOptional<U, E> forValue(U value) {
		if (value == null) {
			return empty();
		}
		return new ValueThrowingOptional<>(value);
	}

	@SuppressWarnings("unchecked")
	public static <U, E extends Throwable> ThrowingOptional<U, E> empty() {
		return (EmptyThrowingOptional<U, E>) EmptyThrowingOptional.INSTANCE;
	}

	public static class MappingThrowingOptional<S, V, E extends Throwable, T extends Throwable, W extends ThrowingOptional<S, E>>
			extends MappingThrowingSupplier<S, V, E, T, W> implements ThrowingOptional<V, T> {

		public MappingThrowingOptional(@NonNull W source,
				@NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
				@NonNull Function<? super E, ? extends T> throwingMapper) {
			super(source, mapper, ThrowingConsumer.ignore(), throwingMapper);
		}

		@Override
		public <R, X extends Throwable> R apply(@NonNull ThrowingFunction<? super V, ? extends R, ? extends X> mapper)
				throws T, X {
			V target = super.get();
			return mapper.apply(target);
		}

		@Override
		public <R> ThrowingOptional<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> mapper) {
			return ThrowingOptional.super.map(mapper);
		}
	}

	default ThrowingOptional<T, E> filter(@NonNull ThrowingPredicate<? super T, ? extends E> filter) {
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

	<R, X extends Throwable> R apply(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper) throws E, X;

	default <X extends Throwable> void ifPresent(ThrowingConsumer<? super T, ? extends X> consumer) throws E, X {
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
	default <R> ThrowingOptional<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return new MappingThrowingOptional<>(this, mapper, Function.identity());
	}

	default T orElse(T other) throws E {
		return apply((e) -> e == null ? other : e);
	}

	default <X extends Throwable> T orElseGet(@NonNull ThrowingSupplier<? extends T, ? extends X> suppler) throws E, X {
		return apply((e) -> e == null ? suppler.get() : e);
	}

	default <X extends Throwable> T orElseThrow(ThrowingSupplier<? extends X, ? extends X> exceptionSupplier)
			throws E, X {
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
