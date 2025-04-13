package run.soeasy.framework.core.function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Pool<T, E extends Throwable> extends Supplier<T, E> {
	public static class PoolPipeline<T, E extends Throwable, W extends Pool<T, E>> extends SupplierPipeline<T, E, W> {

		public PoolPipeline(@NonNull W source, Runnable<? extends E> processor) {
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
		protected final Function<? super S, ? extends T, ? extends E> pipeline;
		protected final Consumer<? super T, ? extends E> endpoint;

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
	default <R> Pool<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedPool<>(this, pipeline, null);
	}

	void close(T target) throws E;

	@Override
	default Pipeline<T, E> onClose(@NonNull Runnable<? extends E> processor) {
		return new PoolPipeline<>(this, processor);
	}

	default Pipeline<T, E> newPipeline() {
		return new PoolPipeline<>(this, null);
	}
}
