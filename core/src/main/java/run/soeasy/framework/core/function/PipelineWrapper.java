package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

public interface PipelineWrapper<T, E extends Throwable, W extends Pipeline<T, E>>
		extends Pipeline<T, E>, ThrowingSupplierWrapper<T, E, W> {
	@Override
	default ThrowingSupplier<T, E> autoCloseable() {
		return getSource().autoCloseable();
	}

	@Override
	default void close() throws E {
		getSource().close();
	}

	@Override
	default Pipeline<T, E> closeable() {
		return getSource().closeable();
	}

	@Override
	default boolean isClosed() {
		return getSource().isClosed();
	}

	@Override
	default <R> Pipeline<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
		return getSource().map(pipeline);
	}

	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
		return getSource().onClose(consumer);
	}

	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
		return getSource().onClose(closeable);
	}

	@Override
	default Pipeline<T, E> singleton() {
		return getSource().singleton();
	}

	@Override
	default <R extends Throwable> Pipeline<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
		return getSource().throwing(throwingMapper);
	}

}
