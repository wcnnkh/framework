package run.soeasy.framework.util.function;

import java.io.Serializable;
import java.util.concurrent.Callable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.lang.Wrapper;
import run.soeasy.framework.util.function.Function.FunctionPipeline;

/**
 * 一个来源的定义
 * 
 * @author shuchaowen
 *
 * @param <T>
 * @param <E>
 */
@FunctionalInterface
public interface Supplier<T, E extends Throwable> {
	public static class FinalSupplier<T, E extends Throwable> extends Wrapped<T>
			implements Supplier<T, E>, Serializable {
		private static final long serialVersionUID = 1L;

		public FinalSupplier(@NonNull T source) {
			super(source);
		}

		@Override
		public T get() throws E {
			return source;
		}
	}

	@RequiredArgsConstructor
	public static class MappedSupplier<S, T, E extends Throwable, W extends Supplier<? extends S, ? extends E>>
			implements Supplier<T, E> {
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
		public <R> Supplier<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> mapper) {
			return new MappedSupplier<>(this.source, (s) -> {
				T target = MappedSupplier.this.mapper.apply(s);
				return mapper.apply(target);
			});
		}
	}

	public static class SupplierPipeline<T, E extends Throwable, W extends Supplier<? extends T, ? extends E>>
			extends FunctionPipeline<T, T, E, W, Function<? super T, ? extends T, ? extends E>> {

		public SupplierPipeline(@NonNull W source, Runnable<? extends E> processor) {
			super(source, Function.identity(), processor);
		}
	}

	@RequiredArgsConstructor
	public static class SourcePool<T, E extends Throwable, W extends Supplier<T, E>> implements Pool<T, E> {
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
	public static interface SupplierWrapper<T, E extends Throwable, W extends Supplier<T, E>>
			extends Supplier<T, E>, Wrapper<W> {
		@Override
		default T get() throws E {
			return getSource().get();
		}

		@Override
		default <R> Supplier<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> mapper) {
			return getSource().map(mapper);
		}
	}

	public static <T, E extends Throwable> Supplier<T, E> of(@NonNull Supplier<? extends T, ? extends E> source) {
		return source::get;
	}

	public static class NativeSupplier<T, E extends Throwable> extends Wrapped<java.util.function.Supplier<? extends T>>
			implements Supplier<T, E> {

		public NativeSupplier(java.util.function.Supplier<? extends T> source) {
			super(source);
		}

		@Override
		public T get() throws E {
			return source.get();
		}
	}

	public static class CallableSource<T> extends Wrapped<Callable<? extends T>> implements Supplier<T, Exception> {

		public CallableSource(Callable<? extends T> source) {
			super(source);
		}

		@Override
		public T get() throws Exception {
			return source.call();
		}
	}

	public static <T, E extends Throwable> Supplier<T, E> forNative(
			@NonNull java.util.function.Supplier<? extends T> supplier) {
		return new NativeSupplier<>(supplier);
	}

	public static <T> Supplier<T, Exception> forCallable(@NonNull Callable<? extends T> callable) {
		return new CallableSource<>(callable);
	}

	public static <T, E extends Throwable> Supplier<T, E> forValue(T value) {
		return new FinalSupplier<T, E>(value);
	}

	T get() throws E;

	default <R> Supplier<R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> mapper) {
		return new MappedSupplier<>(this, mapper);
	}

	default Pipeline<T, E> newPipeline() {
		return new SupplierPipeline<>(this, null);
	}

	default Pool<T, E> onClose(@NonNull Consumer<? super T, ? extends E> endpoint) {
		return new SourcePool<>(this, endpoint);
	}

	default Pipeline<T, E> onClose(@NonNull Runnable<? extends E> processor) {
		return new SupplierPipeline<>(this, processor);
	}
}
