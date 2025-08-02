package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

public interface PoolWrapper<T, E extends Throwable, W extends Pool<T, E>>
		extends Pool<T, E>, ThrowingSupplierWrapper<T, E, W> {

	@Override
	default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
		return getSource().onClose(consumer);
	}

	@Override
	default void close(T source) throws E {
		getSource().close(source);
	}

	@Override
	default Pipeline<T, E> closeable() {
		return getSource().closeable();
	}

	@Override
	default <R> Pipeline<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return getSource().map(mapper);
	}

	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
		return getSource().onClose(closeable);
	}

	@Override
	default ThrowingOptional<T, E> optional() {
		return getSource().optional();
	}

	@Override
	default Pipeline<T, E> singleton() {
		return getSource().singleton();
	}

	@Override
	default <R extends Throwable> Pool<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
		return getSource().throwing(throwingMapper);
	}
}
