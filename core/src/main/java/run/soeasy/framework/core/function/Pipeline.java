package run.soeasy.framework.core.function;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.ThrowingFunction.ThrowingFunctionPipeline;

/**
 * 一个流水线的定义
 * 
 * @author shuchaowen
 *
 * @param <T>
 * @param <E>
 */
public interface Pipeline<T, E extends Throwable> extends ThrowingSupplier<T, E> {
	static class ValuePipeline<T, E extends Throwable> extends ValueThrowingSupplier<T, E> implements Pipeline<T, E> {
		private static final long serialVersionUID = 1L;
		private static final ValuePipeline<?, ?> EMPTY = new ValuePipeline<>(null);

		public ValuePipeline(T value) {
			super(value);
		}

		@Override
		public void close() throws E {
		}

		@Override
		public boolean isClosed() {
			return false;
		}
	}

	public static <T, E extends Throwable> Pipeline<T, E> forValue(T value) {
		return new ValuePipeline<>(value);
	}

	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> Pipeline<T, E> empty() {
		return (Pipeline<T, E>) ValuePipeline.EMPTY;
	}

	public static class MappingPipeline<S, V, E extends Throwable, T extends Throwable, W extends Pipeline<S, E>>
			extends MappingThrowingSupplier<S, V, E, T, W> implements Pipeline<V, T> {

		public MappingPipeline(@NonNull W source, @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
				@NonNull Function<? super E, ? extends T> throwingMapper) {
			super(source, mapper, ThrowingConsumer.ignore(), throwingMapper);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void close() throws T {
			try {
				source.close();
			} catch (Throwable e) {
				throw throwingMapper.apply((E) e);
			}
		}

		@Override
		public boolean isClosed() {
			return source.isClosed();
		}

		@Override
		public <R> Pipeline<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> pipeline) {
			return Pipeline.super.map(pipeline);
		}
	}

	public static class PipelineOptional<S, T, E extends Throwable> extends
			ThrowingFunctionPipeline<S, T, E, ThrowingSupplier<S, E>, ThrowingFunction<? super S, ? extends T, ? extends E>>
			implements ThrowingOptional<T, E> {

		public PipelineOptional(@NonNull ThrowingSupplier<S, E> source,
				@NonNull ThrowingFunction<? super S, ? extends T, ? extends E> pipeline,
				ThrowingRunnable<? extends E> processor) {
			super(source, pipeline, processor);
		}

		@Override
		public T get() throws E {
			return ThrowingOptional.super.get();
		}

		@Override
		public ThrowingOptional<T, E> optional() {
			return this;
		}

		public <R, X extends Throwable> R apply(ThrowingFunction<? super T, ? extends R, ? extends X> mapper)
				throws E, X {
			try {
				T value = super.get();
				return mapper.apply(value);
			} finally {
				close();
			}
		};

		@Override
		public <R> PipelineOptional<T, R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
			return new PipelineOptional<>(super::get, pipeline, this::close);
		}
	}

	public static class PipelinePool<T, E extends Throwable, W extends Pipeline<T, E>>
			extends ThrowingSupplierPool<T, E, W> implements Pool<T, E> {

		public PipelinePool(@NonNull W source, @NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
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
		public <R> Pool<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
			return new MappedChannelPool<>(pipeline);
		}

		@RequiredArgsConstructor
		private class MappedChannelPool<R> implements Pool<R, E> {
			private final ThrowingFunction<? super T, ? extends R, ? extends E> pipeline;
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

	public static class NewPipeline<T, E extends Throwable, W extends Pipeline<T, E>>
			extends ThrowingSupplierPipeline<T, E, W> {

		public NewPipeline(@NonNull W source, ThrowingRunnable<? extends E> processor) {
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

	@Override
	default <R extends Throwable> Pipeline<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingPipeline<>(this, ThrowingFunction.identity(), throwingMapper);
	}

	public static class PipelineRuntimeThrowingSupplier<T, E extends Throwable, R extends RuntimeException, W extends Pipeline<T, E>>
			extends DefaultRuntimeThrowingSupplier<T, E, R, W> {
		private volatile Supplier<?> supplier;

		public PipelineRuntimeThrowingSupplier(@NonNull W source,
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			super(source, throwingMapper);
		}

		@Override
		public T get() throws R {
			if(supplier == null) {
				synchronized (this) {
					if(supplier == null) {
						
					}
				}
			}
			// TODO Auto-generated method stub
			return super.get();
		}
	}

	@Override
	default <R extends RuntimeException> RuntimeThrowingSupplier<T, E, R, ? extends ThrowingSupplier<T, E>> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		// TODO Auto-generated method stub
		return ThrowingSupplier.super.runtime(throwingMapper);
	}

	@Override
	default Optional<T> offline() throws E {
		T value = get();
		try {
			return Optional.ofNullable(value);
		} finally {
			close();
		}
	}

	void close() throws E;

	boolean isClosed();

	@Override
	default <R> Pipeline<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
		return new MappingPipeline<>(this, pipeline, Function.identity());
	}

	/**
	 * 选项
	 * 
	 * @return
	 */
	default ThrowingOptional<T, E> optional() {
		return new PipelineOptional<>(this, ThrowingFunction.identity(), this::close);
	}

	@Override
	default Pipeline<T, E> newPipeline() {
		return new NewPipeline<>(this, null);
	}

	@Override
	default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
		return new PipelinePool<>(this, endpoint);
	}

	@Override
	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> processor) {
		return new NewPipeline<>(this, processor);
	}

	public static <T, E extends Throwable> Pipeline<T, E> forSupplier(
			@NonNull ThrowingSupplier<? extends T, E> supplier) {
		ThrowingSupplier<T, E> source = supplier.map(ThrowingFunction.identity());
		return source.newPipeline();
	}

	public static <T extends AutoCloseable> Pipeline<T, Exception> forAutoCloseable(
			@NonNull ThrowingSupplier<? extends T, ? extends Exception> supplier) {
		ThrowingSupplier<T, Exception> target = () -> supplier.get();
		return target.onClose(AutoCloseable::close).newPipeline();
	}

	public static <T extends Closeable> Pipeline<T, IOException> forCloseable(
			ThrowingSupplier<? extends T, ? extends IOException> source) {
		ThrowingSupplier<T, IOException> target = () -> source.get();
		return target.onClose(Closeable::close).newPipeline();
	}
}
