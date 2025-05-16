package run.soeasy.framework.core.function.stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.function.ThrowingSupplier;
import run.soeasy.framework.core.function.lang.ThrowingConsumer;
import run.soeasy.framework.core.function.lang.ThrowingRunnable;

public interface Pool<T, E extends Throwable> extends ThrowingSupplier<T, E> {
	public static interface PoolWrapper<T, E extends Throwable, W extends Pool<T, E>>
			extends Pool<T, E>, ThrowingSupplierWrapper<T, E, W> {

		@Override
		default Source<T, E> onClose(@NonNull ThrowingRunnable<? extends E> processor) {
			return getSource().onClose(processor);
		}

		@Override
		default Source<T, E> newSupplier() {
			return getSource().newSupplier();
		}

		@Override
		default <R> Pool<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
			return getSource().map(mapper);
		}

		@Override
		default void close(T target) throws E {
			getSource().close(target);
		}
	}

	public static class PoolSource<T, E extends Throwable, W extends Pool<T, E>>
			extends CloseableSupplier<T, E, W> {

		public PoolSource(@NonNull W source, ThrowingRunnable<? extends E> processor) {
			super(source, processor);
		}

		@Override
		public void close() throws E {
			synchronized (this) {
				if (!isClosed()) {
					try {
						super.close();
					} finally {
						source.close(get());
					}
				}
			}
		}
	}

	@RequiredArgsConstructor
	public static class MappedPool<S, T, E extends Throwable, W extends Pool<S, E>> implements Pool<T, E> {
		@NonNull
		protected final W source;
		@NonNull
		protected final ThrowingFunction<? super S, ? extends T, ? extends E> pipeline;
		protected final ThrowingConsumer<? super T, ? extends E> endpoint;

		@Override
		public T get() throws E {
			S target = this.source.get();
			try {
				return pipeline.apply(target);
			} finally {
				source.close(target);
			}
		}

		@Override
		public void close(T target) throws E {
			if (endpoint != null) {
				endpoint.accept(target);
			}
		}
	}

	@Override
	default <R> Pool<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return new MappedPool<>(this, mapper, null);
	}

	void close(T target) throws E;

	@Override
	default Source<T, E> onClose(@NonNull ThrowingRunnable<? extends E> processor) {
		return new PoolSource<>(this, processor);
	}

	default Source<T, E> newSupplier() {
		return new PoolSource<>(this, null);
	}
}
