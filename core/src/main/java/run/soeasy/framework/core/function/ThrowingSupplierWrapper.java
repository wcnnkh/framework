package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

public interface ThrowingSupplierWrapper<T, E extends Throwable, W extends ThrowingSupplier<T, E>>
		extends ThrowingSupplier<T, E>, Wrapper<W> {
	@Override
	default Pipeline<T, E> closeable() {
		return getSource().closeable();
	}

	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
		return getSource().onClose(consumer);
	}

	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
		return getSource().onClose(endpoint);
	}

	@Override
	default <R> ThrowingSupplier<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return getSource().map(mapper);
	}

	@Override
	default <R extends Throwable> ThrowingSupplier<T, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return getSource().throwing(throwingMapper);
	}

	@Override
	default T get() throws E {
		return getSource().get();
	}

	@Override
	default ThrowingOptional<T, E> optional() {
		return getSource().optional();
	}

	@Override
	default Pipeline<T, E> singleton() {
		return getSource().singleton();
	}
}