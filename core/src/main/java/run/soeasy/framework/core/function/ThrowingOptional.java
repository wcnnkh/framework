package run.soeasy.framework.core.function;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.function.lang.ThrowingConsumer;
import run.soeasy.framework.core.function.lang.ThrowingPredicate;

public interface ThrowingOptional<T, E extends Throwable> extends ThrowingSupplier<T, E> {
	public static interface ThrowingOptionalWrapper<T, E extends Throwable, W extends ThrowingOptional<T, E>>
			extends ThrowingOptional<T, E>, ThrowingSupplierWrapper<T, E, W> {
		@Override
		default T get() throws E, NoSuchElementException {
			return getSource().get();
		}

		@Override
		default <R, X extends Throwable> R apply(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			return getSource().apply(mapper);
		}

		@Override
		default <X extends Throwable> void ifPresent(ThrowingConsumer<? super T, ? extends X> consumer) throws E, X {
			getSource().ifPresent(consumer);
		}

		@Override
		default boolean isPresent() throws E {
			return getSource().isPresent();
		}

		@Override
		default <R> ThrowingOptional<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
			return getSource().map(mapper);
		}

		@Override
		default T orElse(T other) throws E {
			return getSource().orElse(other);
		}

		@Override
		default <X extends Throwable> T orElseGet(@NonNull ThrowingSupplier<? extends T, ? extends X> suppler)
				throws E, X {
			return getSource().orElseGet(suppler);
		}

		@Override
		default <X extends Throwable> T orElseThrow(ThrowingSupplier<? extends X, ? extends X> exceptionSupplier)
				throws E, X {
			return getSource().orElseThrow(exceptionSupplier);
		}

		@Override
		default Optional<T> offline() throws E {
			return getSource().offline();
		}
	}

	public static <U, E extends Throwable> ThrowingOptional<U, E> forSupplier(
			@NonNull ThrowingSupplier<? extends U, ? extends E> supplier) {
		return new ThrowingSupplierOptional<>(supplier);
	}

	public static class ValueThrowingOptional<T, E extends Throwable> extends ValueThrowingSupplier<T, E>
			implements ThrowingOptional<T, E> {
		private static final long serialVersionUID = 1L;
		private static final ValueThrowingOptional<?, ?> EMPTY = new ValueThrowingOptional<>(null);

		public ValueThrowingOptional(T value) {
			super(value);
		}

		@Override
		public T get() throws E {
			return ThrowingOptional.super.get();
		}

		@Override
		public <R, X extends Throwable> R apply(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			return mapper.apply(getValue());
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
		return (ValueThrowingOptional<U, E>) ValueThrowingOptional.EMPTY;
	}

	public static class MappingThrowingOptional<S, V, E extends Throwable, T extends Throwable, W extends ThrowingOptional<S, E>>
			extends MappingThrowingSupplier<S, V, E, T, W> implements ThrowingOptional<V, T> {

		public MappingThrowingOptional(@NonNull W source, @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
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

	@Override
	default Optional<T> offline() throws E {
		return apply(Optional::ofNullable);
	}
}
