package io.basc.framework.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Pool<T, E extends Throwable> extends Source<T, E> {
	public static class PoolChannel<T, E extends Throwable, W extends Pool<T, E>> extends SourceChannel<T, E, W> {

		public PoolChannel(@NonNull W source, Processor<? extends E> processor) {
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
		protected final Pipeline<? super S, ? extends T, ? extends E> pipeline;
		protected final Endpoint<? super T, ? extends E> endpoint;

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
	default <R> Pool<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedPool<>(this, pipeline, null);
	}

	void close(T target) throws E;

	@Override
	default Channel<T, E> onClose(@NonNull Processor<? extends E> processor) {
		return new PoolChannel<>(this, processor);
	}

	default Channel<T, E> newChannel() {
		return new PoolChannel<>(this, null);
	}
}
