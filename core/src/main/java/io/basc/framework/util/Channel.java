package io.basc.framework.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Channel<T, E extends Throwable> extends Source<T, E>, Target<T, E> {

	public static class ChannelPool<T, E extends Throwable, W extends Channel<T, E>> extends SourcePool<T, E, W>
			implements Pool<T, E> {

		public ChannelPool(@NonNull W source, @NonNull Endpoint<? super T, ? extends E> endpoint) {
			super(source, endpoint);
		}

		@Override
		public void close(T target) throws E {
			try {
				super.close(target);
			} finally {
				source.close();
			}
		}

		@Override
		public T get() throws E {
			return super.get();
		}

		@Override
		public <R> Pool<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline) {
			return new MappedChannelPool<>(pipeline);
		}

		@RequiredArgsConstructor
		private class MappedChannelPool<R> implements Pool<R, E> {
			private final Pipeline<? super T, ? extends R, ? extends E> pipeline;
			private volatile Supplier<? extends T> targetSupplier;

			@Override
			public R get() throws E {
				if (targetSupplier == null) {
					synchronized (this) {
						if (targetSupplier == null) {
							T target = ChannelPool.this.get();
							targetSupplier = () -> target;
						}
					}
				}

				T target = targetSupplier.get();
				return pipeline.apply(target);
			}

			@Override
			public void close(R target) throws E {
				synchronized (this) {
					if (targetSupplier != null) {
						ChannelPool.this.close(targetSupplier.get());
					}
				}
			}
		}
	}

	public static class MappedChannel<S, T, E extends Throwable, W extends Channel<S, E>>
			extends MappedSource<S, T, E, W> implements Channel<T, E> {

		public MappedChannel(@NonNull W source, @NonNull Pipeline<? super S, ? extends T, ? extends E> mapper) {
			super(source, mapper);
		}

		@Override
		public void close() throws E {
			source.close();
		}

		@Override
		public boolean isClosed() {
			return source.isClosed();
		}

		@Override
		public <R> Channel<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline) {
			return new MappedChannel<>(this, pipeline);
		}

		@Override
		public Pool<T, E> onClose(@NonNull Endpoint<? super T, ? extends E> endpoint) {
			return Channel.super.onClose(endpoint);
		}
	}

	public static class NewChannel<T, E extends Throwable, W extends Channel<T, E>> extends SourceChannel<T, E, W> {

		public NewChannel(@NonNull W source, Processor<? extends E> processor) {
			super(source, processor);
		}

		@Override
		public void close() throws E {
			try {
				super.close();
			} finally {
				source.close();
			}
		}
	}

	void close() throws E;

	boolean isClosed();

	@Override
	default <R> Channel<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedChannel<>(this, pipeline);
	}

	@Override
	default Channel<T, E> newChannel() {
		return new NewChannel<>(this, null);
	}

	@Override
	default Pool<T, E> onClose(@NonNull Endpoint<? super T, ? extends E> endpoint) {
		return new ChannelPool<>(this, endpoint);
	}

	@Override
	default Channel<T, E> onClose(@NonNull Processor<? extends E> processor) {
		return new NewChannel<>(this, processor);
	}

	public static <T, E extends Throwable> Channel<T, E> of(T source) {
		Source<T, E> target = () -> source;
		return target.newChannel();
	}

	public static <T extends AutoCloseable> Channel<T, Exception> forAutoCloseable(
			Source<? extends T, ? extends Exception> source) {
		Source<T, Exception> target = Source.of(source);
		return target.onClose(AutoCloseable::close).newChannel();
	}

	public static <T extends Closeable> Channel<T, IOException> forCloseable(
			Source<? extends T, ? extends IOException> source) {
		Source<T, IOException> target = Source.of(source);
		return target.onClose(Closeable::close).newChannel();
	}
}
