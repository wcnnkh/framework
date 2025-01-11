package io.basc.framework.util.function;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import io.basc.framework.util.function.Function.FunctionPipeline;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 一个来源的定义
 * 
 * @author shuchaowen
 *
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface Source<T, E extends Throwable> {
	public static class FinalSource<T, E extends Throwable> extends Wrapped<T> implements Source<T, E>, Serializable {
		private static final long serialVersionUID = 1L;

		public FinalSource(@NonNull T source) {
			super(source);
		}

		@Override
		public T get() throws E {
			return source;
		}
	}

	@RequiredArgsConstructor
	public static class MappedSource<S, T, E extends Throwable, W extends Source<S, E>> implements Source<T, E> {
		@NonNull
		protected final W source;
		@NonNull
		protected final Function<? super S, ? extends T, ? extends E> mapper;

		@Override
		public T get() throws E {
			S source = this.source.get();
			return mapper.apply(source);
		}

		@Override
		public <R> Source<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> mapper) {
			return new MappedSource<>(this.source, (s) -> {
				T target = MappedSource.this.mapper.apply(s);
				return mapper.apply(target);
			});
		}
	}

	public static class SourcePipeline<T, E extends Throwable, W extends Source<? extends T, ? extends E>>
			extends FunctionPipeline<T, T, E, W, Function<? super T, ? extends T, ? extends E>> {

		public SourcePipeline(@NonNull W source, Runnable<? extends E> processor) {
			super(source, Function.identity(), processor);
		}
	}

	@RequiredArgsConstructor
	public static class SourcePool<T, E extends Throwable, W extends Source<T, E>> implements Pool<T, E> {
		@NonNull
		protected final W source;
		@NonNull
		protected final Consumer<? super T, ? extends E> endpoint;

		@Override
		public void close(T target) throws E {
			endpoint.accept(target);
		}

		@Override
		public T get() throws E {
			return source.get();
		}
	}

	@FunctionalInterface
	public static interface SourceWrapper<T, E extends Throwable, W extends Source<T, E>>
			extends Source<T, E>, Wrapper<W> {
		@Override
		default T get() throws E {
			return getSource().get();
		}

		@Override
		default <R> Source<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> mapper) {
			return getSource().map(mapper);
		}
	}

	public static <T, E extends Throwable> Source<T, E> of(@NonNull Source<? extends T, ? extends E> source) {
		return source::get;
	}

	public static class SupplierSource<T, E extends Throwable> extends Wrapped<Supplier<? extends T>>
			implements Source<T, E> {

		public SupplierSource(Supplier<? extends T> source) {
			super(source);
		}

		@Override
		public T get() throws E {
			return source.get();
		}
	}

	public static class CallableSource<T> extends Wrapped<Callable<? extends T>> implements Source<T, Exception> {

		public CallableSource(Callable<? extends T> source) {
			super(source);
		}

		@Override
		public T get() throws Exception {
			return source.call();
		}
	}

	public static <T, E extends Throwable> Source<T, E> forSupplier(@NonNull Supplier<? extends T> supplier) {
		return new SupplierSource<>(supplier);
	}

	public static <T> Source<T, Exception> forCallable(@NonNull Callable<? extends T> callable) {
		return new CallableSource<>(callable);
	}

	public static <T, E extends Throwable> Source<T, E> of(T value) {
		return new FinalSource<T, E>(value);
	}

	T get() throws E;

	default <R> Source<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedSource<>(this, pipeline);
	}

	default Pipeline<T, E> newPipeline() {
		return new SourcePipeline<>(this, null);
	}

	default Pool<T, E> onClose(@NonNull Consumer<? super T, ? extends E> endpoint) {
		return new SourcePool<>(this, endpoint);
	}

	default Pipeline<T, E> onClose(@NonNull Runnable<? extends E> processor) {
		return new SourcePipeline<>(this, processor);
	}
}
