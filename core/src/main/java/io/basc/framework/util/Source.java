package io.basc.framework.util;

import lombok.Getter;
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
	@Getter
	public static class CloseableSource<T, E extends Throwable, W extends Source<T, E>>
			implements SourceWrapper<T, E, W> {
		@NonNull
		private final W source;
		@NonNull
		private final Endpoint<? super T, ? extends E> closeHandler;

		@Override
		public T get() throws E {
			T target = SourceWrapper.super.get();
			try {
				return target;
			} finally {
				closeHandler.accept(target);
			}
		}

		@Override
		public <R> Source<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> mapper) {
			return new MappedSource<>(this.source, mapper, this.closeHandler);
		}

		@Override
		public Source<T, E> onClose(@NonNull Endpoint<? super T, ? extends E> closeHandler) {
			return new CloseableSource<>(this.source, (s) -> {
				try {
					closeHandler.accept(s);
				} finally {
					Source.CloseableSource.this.closeHandler.accept(s);
				}
			});
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class MappedSource<S, E extends Throwable, W extends Source<S, E>, T>
			implements Source<T, E>, Wrapper<W> {
		@NonNull
		private final W source;
		@NonNull
		private final Pipeline<? super S, ? extends T, ? extends E> mapper;
		private final Endpoint<? super S, ? extends E> closeHandler;

		@Override
		public T get() throws E {
			S source = this.source.get();
			try {
				return mapper.apply(source);
			} finally {
				if (closeHandler != null) {
					closeHandler.accept(source);
				}
			}
		}

		@Override
		public <R> Source<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> mapper) {
			return new MappedSource<>(this.source, (s) -> {
				T target = MappedSource.this.mapper.apply(s);
				return mapper.apply(target);
			}, this.closeHandler);
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
	default <R> Source<R, E> map(@NonNull Pipeline<? super T, ? extends R, ? extends E> mapper) {
		return new MappedSource<>(this, mapper, null);
	}

	default Source<T, E> onClose(@NonNull Endpoint<? super T, ? extends E> closeHandler) {
		return new CloseableSource<>(this, closeHandler);
	}
}
