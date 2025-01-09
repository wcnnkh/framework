package io.basc.framework.util.function;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.collection.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 一个功能的定义
 * 
 * @author shuchaowen
 *
 * @param <S> 数据来源
 * @param <T> 返回的结果
 * @param <E> 异常
 * @see Function
 */
@FunctionalInterface
public interface Function<S, T, E extends Throwable> {

	public static class IdentityFunction<T, E extends Throwable> implements Function<T, T, E> {

		@Override
		public T apply(T source) throws E {
			return source;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <V> Function<T, V, E> map(@NonNull Function<? super T, ? extends V, ? extends E> mapper) {
			return (Function<T, V, E>) mapper;
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class MappedFunction<A, B, E extends Throwable, W extends Function<? super A, ? extends B, ? extends E>, V>
			implements Function<A, V, E>, Wrapper<W> {
		@NonNull
		private final W source;
		@NonNull
		private final Function<? super B, ? extends V, ? extends E> mapper;
		private final Consumer<? super B, ? extends E> closeHandler;

		@Override
		public V apply(A source) throws E {
			B target = this.source.apply(source);
			try {
				return mapper.apply(target);
			} finally {
				if (closeHandler != null) {
					closeHandler.accept(target);
				}
			}
		}

		@Override
		public <R> Function<A, R, E> map(@NonNull Function<? super V, ? extends R, ? extends E> mapper) {
			return new MappedFunction<>(this.source, (s) -> {
				V target = MappedFunction.this.mapper.apply(s);
				return mapper.apply(target);
			}, this.closeHandler);
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class NativeFunction<S, T, E extends Throwable> implements Function<S, T, E> {
		@NonNull
		private final java.util.function.Function<? super S, ? extends T> function;

		@Override
		public T apply(S source) throws E {
			return function.apply(source);
		}

		@Override
		public int hashCode() {
			return function.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj instanceof NativeFunction) {
				NativeFunction<?, ?, ?> other = (NativeFunction<?, ?, ?>) obj;
				return ObjectUtils.equals(this.function, other.function);
			}
			return ObjectUtils.equals(this.function, obj);
		}

		@Override
		public String toString() {
			return function.toString();
		}
	}

	@RequiredArgsConstructor
	public static class FunctionPipeline<S, T, E extends Throwable, W extends Source<? extends S, ? extends E>, P extends Function<? super S, ? extends T, ? extends E>>
			implements Pipeline<T, E> {
		@NonNull
		protected final W source;
		@NonNull
		protected final P pipeline;
		protected final Runnable<? extends E> processor;
		private volatile Supplier<? extends S> supplier;
		private final AtomicBoolean closed = new AtomicBoolean(false);

		@Override
		public void close() throws E {
			synchronized (this) {
				if (closed.compareAndSet(false, true)) {
					if (processor != null) {
						processor.run();
					}
				}
			}
		}

		@Override
		public T get() throws E {
			if (supplier == null) {
				synchronized (this) {
					if (supplier == null) {
						S target = source.get();
						supplier = () -> target;
					}
				}
			}

			S target = supplier.get();
			return pipeline.apply(target);
		}

		@Override
		public boolean isClosed() {
			return closed.get();
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class PipelineReactor<S, T, E extends Throwable, W extends Function<S, T, E>>
			implements Reactor<S, T, E> {
		@NonNull
		private final W source;
		@NonNull
		private final Consumer<? super T, ? extends E> endpoint;

		@Override
		public T apply(S source) throws E {
			T target = this.source.apply(source);
			try {
				return target;
			} finally {
				close(target);
			}
		}

		@Override
		public void close(T target) throws E {
			endpoint.accept(target);
		}

		@Override
		public Reactor<S, T, E> onClose(@NonNull Consumer<? super T, ? extends E> endpoint) {
			return new PipelineReactor<>(this.source, (target) -> {
				try {
					endpoint.accept(target);
				} finally {
					Function.PipelineReactor.this.close(target);
				}
			});
		}
	}

	@FunctionalInterface
	public interface PipelineWrapper<S, T, E extends Throwable, W extends Function<S, T, E>>
			extends Function<S, T, E>, Wrapper<W> {
		@Override
		default T apply(S source) throws E {
			return getSource().apply(source);
		}

		@Override
		default <R> Function<S, R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> mapper) {
			return getSource().map(mapper);
		}
	}

	static final IdentityFunction<?, ?> IDENTITY_FUNCTION = new IdentityFunction<>();

	/**
	 * Returns a function that always returns its input argument.
	 * 
	 * @param <U>
	 * @param <X>
	 * @return a function that always returns its input argument
	 */
	@SuppressWarnings("unchecked")
	static <U, X extends Throwable> Function<U, U, X> identity() {
		return (Function<U, U, X>) IDENTITY_FUNCTION;
	}

	public static <A, B, X extends Throwable> Function<A, B, X> forNative(
			@NonNull java.util.function.Function<? super A, ? extends B> function) {
		return new NativeFunction<>(function);
	}

	T apply(S source) throws E;

	default <R> Function<S, R, E> map(@NonNull Function<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedFunction<>(this, pipeline, null);
	}

	default Pipeline<T, E> newPipeline(@NonNull Source<? extends S, ? extends E> source) {
		return new FunctionPipeline<>(source, this, null);
	}

	default Reactor<S, T, E> onClose(@NonNull Consumer<? super T, ? extends E> endpoint) {
		return new PipelineReactor<>(this, endpoint);
	}

	public static interface Merger<T, E extends Throwable> extends Function<Elements<T>, T, E> {

	}
}
