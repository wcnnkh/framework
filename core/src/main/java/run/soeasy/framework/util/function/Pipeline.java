package run.soeasy.framework.util.function;

import java.io.Closeable;
import java.io.IOException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.function.Function.FunctionPipeline;

/**
 * 一个流水线的定义
 * 
 * @author shuchaowen
 *
 * @param <T>
 * @param <E>
 */
public interface Pipeline<T, E extends Throwable> extends Supplier<T, E> {
	public static class PipelineOptional<S, T, E extends Throwable>
			extends FunctionPipeline<S, T, E, Supplier<S, E>, Function<? super S, ? extends T, ? extends E>>
			implements Optional<T, E> {

		public PipelineOptional(@NonNull Supplier<S, E> source,
				@NonNull Function<? super S, ? extends T, ? extends E> pipeline, Runnable<? extends E> processor) {
			super(source, pipeline, processor);
		}

		@Override
		public T get() throws E {
			return Optional.super.get();
		}

		@Override
		public Optional<T, E> optional() {
			return this;
		}

		public <R, X extends Throwable> R apply(
				run.soeasy.framework.util.function.Function<? super T, ? extends R, ? extends X> mapper) throws E, X {
			try {
				T value = super.get();
				return mapper.apply(value);
			} finally {
				close();
			}
		};

		@Override
		public <R> PipelineOptional<T, R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
			return new PipelineOptional<>(() -> super.get(), pipeline, this::close);
		}
	}

	public static class PipelinePool<T, E extends Throwable, W extends Pipeline<T, E>> extends SourcePool<T, E, W>
			implements Pool<T, E> {

		public PipelinePool(@NonNull W source, @NonNull Consumer<? super T, ? extends E> endpoint) {
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
			private volatile java.util.function.Supplier<? extends T> targetSupplier;

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
			extends MappedSupplier<S, T, E, W> implements Pipeline<T, E> {

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
		public Pool<T, E> onClose(@NonNull Consumer<? super T, ? extends E> endpoint) {
			return Pipeline.super.onClose(endpoint);
		}
	}

	public static class NewPipeline<T, E extends Throwable, W extends Pipeline<T, E>>
			extends SupplierPipeline<T, E, W> {

		public NewPipeline(@NonNull W source, Runnable<? extends E> processor) {
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

	@FunctionalInterface
	public static interface PipelineWrapper<T, E extends Throwable, W extends Pipeline<T, E>>
			extends Pipeline<T, E>, SupplierWrapper<T, E, W> {
		@Override
		default void close() throws E {
			getSource().close();
		}

		@Override
		default boolean isClosed() {
			return getSource().isClosed();
		}

		@Override
		default <R> Pipeline<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
			return getSource().map(pipeline);
		}

		@Override
		default Optional<T, E> optional() {
			return getSource().optional();
		}

		@Override
		default Pipeline<T, E> newPipeline() {
			return getSource().newPipeline();
		}

		@Override
		default Pool<T, E> onClose(@NonNull Consumer<? super T, ? extends E> endpoint) {
			return getSource().onClose(endpoint);
		}

		@Override
		default Pipeline<T, E> onClose(@NonNull Runnable<? extends E> processor) {
			return getSource().onClose(processor);
		}
	}

	void close() throws E;

	boolean isClosed();

	@Override
	default <R> Pipeline<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedPipeline<>(this, pipeline);
	}

	/**
	 * 选项
	 * 
	 * @return
	 */
	default Optional<T, E> optional() {
		return new PipelineOptional<>(this, Function.identity(), this::close);
	}

	@Override
	default Pipeline<T, E> newPipeline() {
		return new NewPipeline<>(this, null);
	}

	@Override
	default Pool<T, E> onClose(@NonNull Consumer<? super T, ? extends E> endpoint) {
		return new PipelinePool<>(this, endpoint);
	}

	@Override
	default Pipeline<T, E> onClose(@NonNull Runnable<? extends E> processor) {
		return new NewPipeline<>(this, processor);
	}

	public static <T, E extends Throwable> Pipeline<T, E> forValue(T value) {
		Supplier<T, E> source = Supplier.forValue(value);
		return source.newPipeline();
	}

	public static <T, E extends Throwable> Pipeline<T, E> of(@NonNull Supplier<? extends T, E> supplier) {
		Supplier<T, E> source = supplier.map(Function.identity());
		return source.newPipeline();
	}

	public static <T extends AutoCloseable> Pipeline<T, Exception> forAutoCloseable(
			@NonNull Supplier<? extends T, ? extends Exception> supplier) {
		Supplier<T, Exception> target = Supplier.of(supplier);
		return target.onClose(AutoCloseable::close).newPipeline();
	}

	public static <T extends Closeable> Pipeline<T, IOException> forCloseable(
			Supplier<? extends T, ? extends IOException> source) {
		Supplier<T, IOException> target = Supplier.of(source);
		return target.onClose(Closeable::close).newPipeline();
	}
}
