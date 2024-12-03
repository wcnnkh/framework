package io.basc.framework.util;

import io.basc.framework.util.Pipeline.PipelineChannel;
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
	public static class MappedSource<S, T, E extends Throwable, W extends Source<S, E>> implements Source<T, E> {
		@NonNull
		protected final W source;
		@NonNull
		protected final Pipeline<? super S, ? extends T, ? extends E> mapper;

		@Override
		public T get() throws E {
			S source = this.source.get();
			return mapper.apply(source);
		}

		@Override
		public <R> Source<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> mapper) {
			return new MappedSource<>(this.source, (s) -> {
				T target = MappedSource.this.mapper.apply(s);
				return mapper.apply(target);
			});
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
		default <R> Source<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> mapper) {
			return getSource().map(mapper);
		}
	}

	T get() throws E;

	/*
	 * 对结果进行映射
	 */
	default <R> Source<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> pipeline) {
		return new MappedSource<>(this, pipeline);
	}

	@RequiredArgsConstructor
	public static class SourcePool<T, E extends Throwable, W extends Source<T, E>> implements Pool<T, E> {
		@NonNull
		protected final W source;
		@NonNull
		protected final Endpoint<? super T, ? extends E> endpoint;

		@Override
		public T get() throws E {
			return source.get();
		}

		@Override
		public void close(T target) throws E {
			endpoint.accept(target);
		}
	}

	public static class SourceChannel<T, E extends Throwable, W extends Source<? extends T, ? extends E>>
			extends PipelineChannel<T, T, E, W, Pipeline<? super T, ? extends T, ? extends E>> {

		public SourceChannel(@NonNull W source, Processor<? extends E> processor) {
			super(source, Pipeline.identity(), processor);
		}
	}

	default Channel<T, E> newChannel() {
		return new SourceChannel<>(this, null);
	}

	default Pool<T, E> onClose(@NonNull Endpoint<? super T, ? extends E> endpoint) {
		return new SourcePool<>(this, endpoint);
	}

	default Channel<T, E> onClose(@NonNull Processor<? extends E> processor) {
		return new SourceChannel<>(this, processor);
	}
}
