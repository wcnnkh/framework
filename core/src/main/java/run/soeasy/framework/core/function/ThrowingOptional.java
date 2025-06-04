package run.soeasy.framework.core.function;

import java.util.NoSuchElementException;
import java.util.function.Function;

import lombok.NonNull;

public interface ThrowingOptional<T, E extends Throwable> extends ThrowingSupplier<T, E> {
	public static <U, E extends Throwable> ThrowingOptional<U, E> forSupplier(
			@NonNull ThrowingSupplier<U, E> supplier) {
		if (supplier instanceof ThrowingOptional) {
			return (ThrowingOptional<U, E>) supplier;
		}
		return supplier.optional();
	}

	public static <U, E extends Throwable> ThrowingOptional<U, E> forValue(U value) {
		if (value == null) {
			return empty();
		}
		return new ValueThrowingOptional<>(value);
	}

	@SuppressWarnings("unchecked")
	public static <U, E extends Throwable> ThrowingOptional<U, E> empty() {
		return (ThrowingOptional<U, E>) ValueThrowingOptional.EMPTY;
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

	<R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
			throws E, X;

	default <X extends Throwable> void ifPresent(ThrowingConsumer<? super T, ? extends X> consumer) throws E, X {
		flatMap((e) -> {
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
		return new MappingThrowingOptional<>(this, mapper, ThrowingConsumer.ignore(), Function.identity(), false,
				ThrowingRunnable.ignore());
	}

	default T orElse(T other) throws E {
		return flatMap((e) -> e == null ? other : e);
	}

	default <X extends Throwable> T orElseGet(@NonNull ThrowingSupplier<? extends T, ? extends X> suppler) throws E, X {
		return flatMap((e) -> e == null ? suppler.get() : e);
	}

	default <X extends Throwable> T orElseThrow(ThrowingSupplier<? extends X, ? extends X> exceptionSupplier)
			throws E, X {
		return flatMap((e) -> {
			if (e == null) {
				throw exceptionSupplier.get();
			}
			return e;
		});
	}

	@Override
	default ThrowingOptional<T, E> optional() {
		return this;
	}
}
