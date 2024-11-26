package io.basc.framework.util;

import java.util.function.Function;

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

	@RequiredArgsConstructor
	@Getter
	public static class CloseablePipeline<S, T, E extends Throwable, W extends Pipeline<S, T, E>>
			implements PipelineWrapper<S, T, E, W> {
		@NonNull
		private final W source;
		@NonNull
		private final Endpoint<? super T, ? extends E> closeHandler;

		@Override
		public T apply(S source) throws E {
			T target = PipelineWrapper.super.apply(source);
			try {
				return target;
			} finally {
				closeHandler.accept(target);
			}
		}

		@Override
		public Pipeline<S, T, E> onClose(@NonNull Endpoint<? super T, ? extends E> closeHandler) {
			return new CloseablePipeline<>(this.source, (s) -> {
				try {
					closeHandler.accept(s);
				} finally {
					Pipeline.CloseablePipeline.this.closeHandler.accept(s);
				}
			});
		}

		@Override
		public <R> Pipeline<S, R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> mapper) {
			return new MappedPipeline<>(this.source, mapper, this.closeHandler);
		}
	}

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

	default <R> Pipeline<S, R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> mapper) {
		return new MappedPipeline<>(this, mapper, null);
	}

	default Pipeline<S, T, E> onClose(@NonNull Endpoint<? super T, ? extends E> closeHandler) {
		return new CloseablePipeline<>(this, closeHandler);
	}
}
