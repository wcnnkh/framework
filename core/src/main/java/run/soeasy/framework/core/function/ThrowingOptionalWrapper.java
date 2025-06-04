package run.soeasy.framework.core.function;

import java.util.NoSuchElementException;

import lombok.NonNull;

public interface ThrowingOptionalWrapper<T, E extends Throwable, W extends ThrowingOptional<T, E>>
		extends ThrowingOptional<T, E>, ThrowingSupplierWrapper<T, E, W> {
	@Override
	default T get() throws E, NoSuchElementException {
		return getSource().get();
	}

	@Override
	default <R, X extends Throwable> R flatMap(@NonNull ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
			throws E, X {
		return getSource().flatMap(mapper);
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
	default <X extends Throwable> T orElseGet(@NonNull ThrowingSupplier<? extends T, ? extends X> suppler) throws E, X {
		return getSource().orElseGet(suppler);
	}

	@Override
	default <X extends Throwable> T orElseThrow(ThrowingSupplier<? extends X, ? extends X> exceptionSupplier)
			throws E, X {
		return getSource().orElseThrow(exceptionSupplier);
	}

	@Override
	default ThrowingOptional<T, E> optional() {
		return getSource().optional();
	}
}