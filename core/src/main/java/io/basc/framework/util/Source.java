package io.basc.framework.util;

import java.io.Serializable;

import io.basc.framework.util.Function.FunctionPipeline;
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
	@RequiredArgsConstructor
	public static class FinalSource<T, E extends Throwable> implements Source<T, E>, Serializable {
		private static final long serialVersionUID = 1L;
		private final T value;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FinalSource) {
				FinalSource<?, ?> source = (FinalSource<?, ?>) obj;
				return ObjectUtils.equals(value, source.value);
			}
			return ObjectUtils.equals(value, obj);
		}

		@Override
		public T get() throws E {
			return value;
		}

		@Override
		public int hashCode() {
			return value == null ? 0 : value.hashCode();
		}

		@Override
		public String toString() {
			return value == null ? null : value.toString();
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

		public SourcePipeline(@NonNull W source, Processor<? extends E> processor) {
			super(source, Function.identity(), processor);
		}
	}

	@RequiredArgsConstructor
	public static class SourcePool<T, E extends Throwable, W extends Source<T, E>> implements Pool<T, E> {
		@NonNull
		protected final W source;
		@NonNull
		protected final Endpoint<? super T, ? extends E> endpoint;

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

	default Pool<T, E> onClose(@NonNull Endpoint<? super T, ? extends E> endpoint) {
		return new SourcePool<>(this, endpoint);
	}

	default Pipeline<T, E> onClose(@NonNull Processor<? extends E> processor) {
		return new SourcePipeline<>(this, processor);
	}
}
