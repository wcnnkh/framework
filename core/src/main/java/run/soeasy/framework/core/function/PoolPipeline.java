package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

class PoolPipeline<S, V, E extends Throwable, T extends Throwable, W extends ThrowingSupplier<S, E>>
		extends ChainPipeline<S, V, E, T, W> {

	public PoolPipeline(@NonNull W source, @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
			ThrowingConsumer<? super S, ? extends E> endpoint, @NonNull Function<? super E, ? extends T> throwingMapper,
			ThrowingRunnable<? extends E> closeable) {
		super(source, mapper, endpoint, throwingMapper, true, closeable);
	}
	
}
