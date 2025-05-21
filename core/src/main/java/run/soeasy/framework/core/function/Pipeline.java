package run.soeasy.framework.core.function;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Function;

import lombok.NonNull;

/**
 * 一个流水线的定义
 * 
 * @author shuchaowen
 *
 * @param <T>
 * @param <E>
 */
public interface Pipeline<T, E extends Throwable> extends ThrowingSupplier<T, E> {
	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> Pipeline<T, E> empty() {
		return (Pipeline<T, E>) EmptyPipeline.INSTANCE;
	}

	public static <T, E extends Throwable> Pipeline<T, E> forSupplier(ThrowingSupplier<T, E> supplier) {
		return supplier.closeable();
	}

	public static <T extends AutoCloseable> Pipeline<T, Exception> forAutoCloseable(
			ThrowingSupplier<T, Exception> autoCloseableSupplier) {
		return autoCloseableSupplier.onClose(AutoCloseable::close).autoCloseable().closeable();
	}

	public static <T extends Closeable> Pipeline<T, IOException> forCloseable(
			ThrowingSupplier<T, IOException> closeableSupplier) {
		return closeableSupplier.onClose(Closeable::close).autoCloseable().closeable();
	}

	default ThrowingSupplier<T, E> autoCloseable() {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				Function.identity(), false, this::close);
	}

	void close() throws E;

	@Override
	default Pipeline<T, E> closeable() {
		return this;
	}

	boolean isClosed();

	@Override
	default <R> Pipeline<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
		return new MappingPipeline<>(this, pipeline, ThrowingConsumer.ignore(), Function.identity(), false,
				ThrowingRunnable.ignore());
	}

	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
		return new MappingPipeline<>(this, ThrowingFunction.identity(), consumer, Function.identity(), false,
				ThrowingRunnable.ignore());
	}

	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
		return new MappingPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), Function.identity(),
				false, closeable);
	}

	@Override
	default Pipeline<T, E> singleton() {
		return new MappingPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), Function.identity(),
				true, ThrowingRunnable.ignore());
	}

	@Override
	default <R extends Throwable> Pipeline<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingPipeline<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(), throwingMapper,
				false, ThrowingRunnable.ignore());
	}
}
