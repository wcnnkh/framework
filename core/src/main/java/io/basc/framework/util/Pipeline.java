package io.basc.framework.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public interface Pipeline<T, E extends Throwable> extends Source<T, E>, Target<T, E> {
	@RequiredArgsConstructor
	public static class PipelineOptional<T, E extends Throwable, W extends Pipeline<T, E>> implements Optional<T, E> {
		private final W source;
		private volatile java.util.Optional<T> optional;

		@Override
		public T orElse(T other) throws E {
			if (optional == null) {
				synchronized (this) {
					if (optional == null) {
						optional = source.finish();
					}
				}
			}
			return optional.orElse(other);
		}
	}

	public static class PipelinePool<T, E extends Throwable, W extends Pipeline<T, E>> extends SourcePool<T, E, W>
			implements Pool<T, E> {

		public PipelinePool(@NonNull W source, @NonNull Endpoint<? super T, ? extends E> endpoint) {
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
		public <R> Pool<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
			return new MappedChannelPool<>(pipeline);
		}

		@RequiredArgsConstructor
		private class MappedChannelPool<R> implements Pool<R, E> {
			private final Function<? super T, ? extends R, ? extends E> pipeline;
			private volatile Supplier<? extends T> targetSupplier;

			@Override
			public R get() throws E {
				if (targetSupplier == null) {
					synchronized (this) {
						if (targetSupplier == null) {
							T target = PipelinePool.this.get();
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
						PipelinePool.this.close(targetSupplier.get());
					}
				}
			}
		}
	}

	public static class MappedPipeline<S, T, E extends Throwable, W extends Pipeline<S, E>>
			extends MappedSource<S, T, E, W> implements Pipeline<T, E> {

		public MappedPipeline(@NonNull W source, @NonNull Function<? super S, ? extends T, ? extends E> mapper) {
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
		public <R> Pipeline<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
			return new MappedPipeline<>(this, pipeline);
		}

		@Override
		public Pool<T, E> onClose(@NonNull Endpoint<? super T, ? extends E> endpoint) {
			return Pipeline.super.onClose(endpoint);
		}
	}

	public static class NewPipeline<T, E extends Throwable, W extends Pipeline<T, E>> extends SourcePipeline<T, E, W> {

		public NewPipeline(@NonNull W source, Processor<? extends E> processor) {
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
	default <R> Pipeline<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedPipeline<>(this, pipeline);
	}

	default Optional<T, E> export() {
		return new PipelineOptional<>(this);
	}

	default java.util.Optional<T> finish() throws E {
		try {
			T value = get();
			return java.util.Optional.ofNullable(value);
		} finally {
			close();
		}
	}

	@Override
	default Pipeline<T, E> newPipeline() {
		return new NewPipeline<>(this, null);
	}

	@Override
	default Pool<T, E> onClose(@NonNull Endpoint<? super T, ? extends E> endpoint) {
		return new PipelinePool<>(this, endpoint);
	}

	@Override
	default Pipeline<T, E> onClose(@NonNull Processor<? extends E> processor) {
		return new NewPipeline<>(this, processor);
	}

	public static <T, E extends Throwable> Pipeline<T, E> of(T value) {
		Source<T, E> source = Source.of(value);
		return source.newPipeline();
	}

	public static <T extends AutoCloseable> Pipeline<T, Exception> forAutoCloseable(
			Source<? extends T, ? extends Exception> source) {
		Source<T, Exception> target = Source.of(source);
		return target.onClose(AutoCloseable::close).newPipeline();
	}

	public static <T extends Closeable> Pipeline<T, IOException> forCloseable(
			Source<? extends T, ? extends IOException> source) {
		Source<T, IOException> target = Source.of(source);
		return target.onClose(Closeable::close).newPipeline();
	}
}
