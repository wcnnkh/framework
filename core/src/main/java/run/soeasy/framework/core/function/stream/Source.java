package run.soeasy.framework.core.function.stream;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.function.ThrowingFunction.ThrowingFunctionToSource;
import run.soeasy.framework.core.function.lang.ThrowingConsumer;
import run.soeasy.framework.core.function.lang.ThrowingRunnable;
import run.soeasy.framework.core.function.ThrowingOptional;
import run.soeasy.framework.core.function.ThrowingSupplier;
import run.soeasy.framework.core.function.runtime.RuntimeCloseableSupplier;

/**
 * 一个流水线的定义
 * 
 * @author shuchaowen
 *
 * @param <T>
 * @param <E>
 */
public interface Source<T, E extends Throwable> extends ThrowingSupplier<T, E> {
	public static interface SourceWrapper<T, E extends Throwable, W extends Source<T, E>>
			extends Source<T, E>, ThrowingSupplierWrapper<T, E, W> {

		@Override
		default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
			return getSource().onClose(endpoint);
		}

		@Override
		default Source<T, E> onClose(@NonNull ThrowingRunnable<? extends E> processor) {
			return getSource().onClose(processor);
		}

		@Override
		default Source<T, E> closeable() {
			return getSource().closeable();
		}

		@Override
		default Optional<T> offline() throws E {
			return getSource().offline();
		}

		@Override
		default RuntimeCloseableSupplier<T, RuntimeException> runtime() {
			return getSource().runtime();
		}

		@Override
		default <R extends Throwable> Source<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
			return getSource().throwing(throwingMapper);
		}

		@Override
		default <R> Source<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
			return getSource().map(pipeline);
		}

		@Override
		default ThrowingOptional<T, E> optional() {
			return getSource().optional();
		}

		default <R extends Exception> Callable<T> asCallable(Function<? super E, ? extends R> throwingMapper) {
			return getSource().asCallable(throwingMapper);
		}
	}

	static class ValueSource<T, E extends Throwable> extends ValueThrowingSupplier<T, E> implements Source<T, E> {
		private static final long serialVersionUID = 1L;
		private static final ValueSource<?, ?> EMPTY = new ValueSource<>(null);

		public ValueSource(T value) {
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

	public static <T, E extends Throwable> Source<T, E> forValue(T value) {
		return new ValueSource<>(value);
	}

	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> Source<T, E> empty() {
		return (Source<T, E>) ValueSource.EMPTY;
	}

	public static class MappingSource<S, V, E extends Throwable, T extends Throwable, W extends Source<S, E>>
			extends MappingThrowingSupplier<S, V, E, T, W> implements Source<V, T> {

		public MappingSource(@NonNull W source, @NonNull ThrowingFunction<? super S, ? extends V, T> mapper,
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
		public <R> Source<R, T> map(@NonNull ThrowingFunction<? super V, ? extends R, T> pipeline) {
			return Source.super.map(pipeline);
		}
	}

	public static class CloseableThrowingOptional<S, T, E extends Throwable> extends
			ThrowingFunctionToSource<S, T, E, ThrowingSupplier<S, E>, ThrowingFunction<? super S, ? extends T, ? extends E>>
			implements ThrowingOptional<T, E> {

		public CloseableThrowingOptional(@NonNull ThrowingSupplier<S, E> source,
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
		public <R> CloseableThrowingOptional<T, R, E> map(
				@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
			return new CloseableThrowingOptional<>(super::get, pipeline, this::close);
		}

		@Override
		public Optional<T> offline() throws E {
			return super.offline();
		}
	}

	public static class SourcePool<T, E extends Throwable, W extends Source<T, E>> extends ThrowingSupplierPool<T, E, W>
			implements Pool<T, E> {

		public SourcePool(@NonNull W source, @NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
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
							T target = SourcePool.this.get();
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
						SourcePool.this.close(targetSupplier.get());
					}
				}
			}
		}
	}

	public static class NewSource<T, E extends Throwable, W extends Source<T, E>> extends CloseableSupplier<T, E, W> {

		public NewSource(@NonNull W source, ThrowingRunnable<? extends E> processor) {
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
	default <R extends Throwable> Source<T, R> throwing(@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingSource<>(this, ThrowingFunction.identity(), throwingMapper);
	}

	public static class RuntimeSource<T, E extends Throwable, R extends RuntimeException, W extends Source<T, E>>
			extends MappingSource<T, T, E, R, W> implements RuntimeCloseableSupplier<T, R> {

		public RuntimeSource(@NonNull W source, @NonNull Function<? super E, ? extends R> throwingMapper) {
			super(source, ThrowingFunction.identity(), throwingMapper);
		}
	}

	@Override
	default <R extends RuntimeException> RuntimeCloseableSupplier<T, R> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new RuntimeSource<>(this, throwingMapper);
	}

	@Override
	default RuntimeCloseableSupplier<T, RuntimeException> runtime() {
		return runtime((e) -> e instanceof RuntimeException ? ((RuntimeException) e) : new RuntimeException(e));
	}

	@RequiredArgsConstructor
	public static class SourceAsCallable<T, E extends Exception, W extends Source<T, E>> implements Callable<T> {
		@NonNull
		private final W source;

		@Override
		public T call() throws E {
			try {
				return source.get();
			} finally {
				source.close();
			}
		}
	}

	@Override
	default <R extends Exception> Callable<T> asCallable(@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new SourceAsCallable<>(this.throwing(throwingMapper));
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
	default <R> Source<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> pipeline) {
		return new MappingSource<>(this, pipeline, Function.identity());
	}

	/**
	 * 选项
	 * 
	 * @return
	 */
	default ThrowingOptional<T, E> optional() {
		return new CloseableThrowingOptional<>(this, ThrowingFunction.identity(), this::close);
	}

	@Override
	default Source<T, E> closeable() {
		return this;
	}

	@Override
	default Pool<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
		return new SourcePool<>(this, endpoint);
	}

	@Override
	default Source<T, E> onClose(@NonNull ThrowingRunnable<? extends E> processor) {
		return new NewSource<>(this, processor);
	}

	public static <T, E extends Throwable> Source<T, E> forSupplier(
			@NonNull ThrowingSupplier<? extends T, E> supplier) {
		ThrowingSupplier<T, E> source = supplier.map(ThrowingFunction.identity());
		return source.closeable();
	}

	public static <T extends AutoCloseable> Source<T, Exception> forAutoCloseable(
			@NonNull ThrowingSupplier<? extends T, ? extends Exception> supplier) {
		ThrowingSupplier<T, Exception> target = () -> supplier.get();
		return target.onClose(AutoCloseable::close).newSupplier();
	}

	public static <T extends Closeable> Source<T, IOException> forCloseable(
			ThrowingSupplier<? extends T, ? extends IOException> source) {
		ThrowingSupplier<T, IOException> target = () -> source.get();
		return target.onClose(Closeable::close).newSupplier();
	}
}
