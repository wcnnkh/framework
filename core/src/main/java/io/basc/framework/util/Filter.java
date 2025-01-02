package io.basc.framework.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@FunctionalInterface
public interface Filter<T, E extends Throwable> {

	@FunctionalInterface
	public static interface FilterWrapper<T, E extends Throwable, W extends Filter<T, E>>
			extends Filter<T, E>, io.basc.framework.util.Wrapper<W> {
		@Override
		default boolean test(T source) throws E {
			return getSource().test(source);
		}

		@Override
		default <S> Filter<S, E> map(@NonNull Function<? super S, ? extends T, ? extends E> mapper) {
			return getSource().map(mapper);
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class MappedFilter<S, T, E extends Throwable, W extends Filter<S, E>>
			implements Filter<T, E>, io.basc.framework.util.Wrapper<W> {
		private final W source;
		private final Function<? super T, ? extends S, ? extends E> mapper;

		@Override
		public boolean test(T source) throws E {
			S target = mapper.apply(source);
			return this.source.test(target);
		}
	}

	default <S> Filter<S, E> map(@NonNull Function<? super S, ? extends T, ? extends E> mapper) {
		return new MappedFilter<>(this, mapper);
	}

	boolean test(T source) throws E;
}
