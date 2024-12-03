package io.basc.framework.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 一个流水线的定义
 * 
 * @author shuchaowen
 *
 * @param <S> 数据来源
 * @param <T> 返回的结果
 * @param <E> 异常
 * @see Function
 */
@FunctionalInterface
public interface Pipeline<S, T, E extends Throwable> {

	public static class IdentityPipeline<T, E extends Throwable> implements Pipeline<T, T, E> {

		@Override
		public T apply(T source) throws E {
			return source;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <V> Pipeline<T, V, E> map(@NonNull Pipeline<? super T, ? extends V, ? extends E> mapper) {
			return (Pipeline<T, V, E>) mapper;
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class MappedPipeline<A, B, E extends Throwable, W extends Pipeline<? super A, ? extends B, ? extends E>, V>
			implements Pipeline<A, V, E>, Wrapper<W> {
		@NonNull
		private final W source;
		@NonNull
		private final Pipeline<? super B, ? extends V, ? extends E> mapper;
		private final Endpoint<? super B, ? extends E> closeHandler;

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
		public <R> Pipeline<A, R, E> map(@NonNull Pipeline<? super V, ? extends R, ? extends E> mapper) {
			return new MappedPipeline<>(this.source, (s) -> {
				V target = MappedPipeline.this.mapper.apply(s);
				return mapper.apply(target);
			}, this.closeHandler);
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class NativePipeline<S, T, E extends Throwable> implements Pipeline<S, T, E> {
		@NonNull
		private final Function<? super S, ? extends T> function;

		@Override
		public T apply(S source) throws E {
			return function.apply(source);
		}
	}

	@RequiredArgsConstructor
	public static class PipelineChannel<S, T, E extends Throwable, W extends Source<? extends S, ? extends E>, P extends Pipeline<? super S, ? extends T, ? extends E>>
			implements Channel<T, E> {
		@NonNull
		protected final W source;
		@NonNull
		protected final P pipeline;
		protected final Processor<? extends E> processor;
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
	public static class PipelineReactor<S, T, E extends Throwable, W extends Pipeline<S, T, E>>
			implements Reactor<S, T, E> {
		@NonNull
		private final W source;
		@NonNull
		private final Endpoint<? super T, ? extends E> endpoint;

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
		public Reactor<S, T, E> onClose(@NonNull Endpoint<? super T, ? extends E> endpoint) {
			return new PipelineReactor<>(this.source, (target) -> {
				try {
					endpoint.accept(target);
				} finally {
					Pipeline.PipelineReactor.this.close(target);
				}
			});
		}
	}

	@FunctionalInterface
	public interface PipelineWrapper<S, T, E extends Throwable, W extends Pipeline<S, T, E>>
			extends Pipeline<S, T, E>, Wrapper<W> {
		@Override
		default T apply(S source) throws E {
			return getSource().apply(source);
		}

		@Override
		default <R> Pipeline<S, R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> mapper) {
			return getSource().map(mapper);
		}
	}

	static final IdentityPipeline<?, ?> IDENTITY_PIPELINE = new IdentityPipeline<>();

	/**
	 * Returns a function that always returns its input argument.
	 * 
	 * @param <U>
	 * @param <X>
	 * @return a function that always returns its input argument
	 */
	@SuppressWarnings("unchecked")
	static <U, X extends Throwable> Pipeline<U, U, X> identity() {
		return (Pipeline<U, U, X>) IDENTITY_PIPELINE;
	}

	public static <A, B, X extends Throwable> Pipeline<A, B, X> of(Function<? super A, ? extends B> function) {
		return new NativePipeline<>(function);
	}

	T apply(S source) throws E;

	default <R> Pipeline<S, R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedPipeline<>(this, pipeline, null);
	}

	default Channel<T, E> newChannel(@NonNull Source<? extends S, ? extends E> source) {
		return new PipelineChannel<>(source, this, null);
	}

	default Reactor<S, T, E> onClose(@NonNull Endpoint<? super T, ? extends E> endpoint) {
		return new PipelineReactor<>(this, endpoint);
	}
}
