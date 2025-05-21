package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

class MappingPipeline<S, V, E extends Throwable, T extends Throwable, W extends Pipeline<? extends S, ? extends E>>
		extends MappingThrowingSupplier<S, V, E, T, W> {

	public MappingPipeline(@NonNull W source, @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
			ThrowingConsumer<? super S, ? extends E> endpoint, @NonNull Function<? super E, ? extends T> throwingMapper,
			boolean singleton, @NonNull ThrowingRunnable<? extends T> closeable) {
		super(source, mapper, endpoint, throwingMapper, singleton, closeable);
	}

	@Override
	public boolean isClosed() {
		return super.isClosed() || source.isClosed();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void close() throws T {
		try {
			source.close();
		} catch (Throwable e) {
			throw throwingMapper.apply((E) e);
		} finally {
			super.close();
		}
	}
}