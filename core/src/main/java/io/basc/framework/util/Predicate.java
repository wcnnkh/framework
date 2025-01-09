package io.basc.framework.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@FunctionalInterface
public interface Predicate<T, E extends Throwable> {

	@FunctionalInterface
	public static interface PredicateWrapper<T, E extends Throwable, W extends Predicate<T, E>>
			extends Predicate<T, E>, Wrapper<W> {
		@Override
		default boolean test(T source) throws E {
			return getSource().test(source);
		}

		@Override
		default <S> Predicate<S, E> map(@NonNull Function<? super S, ? extends T, ? extends E> mapper) {
			return getSource().map(mapper);
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class MappedPredicate<S, T, E extends Throwable, W extends Predicate<S, E>>
			implements Predicate<T, E>, Wrapper<W> {
		private final W source;
		private final Function<? super T, ? extends S, ? extends E> mapper;

		@Override
		public boolean test(T source) throws E {
			S target = mapper.apply(source);
			return this.source.test(target);
		}
	}

	@Getter
	@RequiredArgsConstructor
	public static class NativePredicate<S, E extends Throwable> implements Predicate<S, E> {
		@NonNull
		private final java.util.function.Predicate<? super S> predicate;

		@Override
		public int hashCode() {
			return predicate.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (obj instanceof NativePredicate) {
				NativePredicate<?, ?> other = (NativePredicate<?, ?>) obj;
				return ObjectUtils.equals(this.predicate, other.predicate);
			}
			return ObjectUtils.equals(this.predicate, obj);
		}

		@Override
		public String toString() {
			return predicate.toString();
		}

		@Override
		public boolean test(S source) throws E {
			return predicate.test(source);
		}
	}

	default <S> Predicate<S, E> map(@NonNull Function<? super S, ? extends T, ? extends E> mapper) {
		return new MappedPredicate<>(this, mapper);
	}

	public static <T, E extends Throwable> Predicate<T, E> forNative(
			@NonNull java.util.function.Predicate<? super T> predicate) {
		return new NativePredicate<>(predicate);
	}

	boolean test(T source) throws E;
}
