package io.basc.framework.util.function;

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

	public static class NativePredicate<S, E extends Throwable> extends Wrapped<java.util.function.Predicate<? super S>>
			implements Predicate<S, E> {

		public NativePredicate(java.util.function.Predicate<? super S> source) {
			super(source);
		}

		@Override
		public boolean test(S source) throws E {
			return this.source.test(source);
		}
	}

	default <S> Predicate<S, E> map(@NonNull Function<? super S, ? extends T, ? extends E> mapper) {
		return new MappedPredicate<>(this, mapper);
	}

	public static <T, E extends Throwable> Predicate<T, E> forNative(
			@NonNull java.util.function.Predicate<? super T> predicate) {
		return new NativePredicate<>(predicate);
	}

	@RequiredArgsConstructor
	public static class BooleanPredicat<T, E extends Throwable> implements Predicate<T, E> {
		private static final BooleanPredicat<?, ?> ALWAYS_TRUE_PREDICATE = new BooleanPredicat<>(true);
		private static final BooleanPredicat<?, ?> ALWAYS_FALSE_PREDICATE = new BooleanPredicat<>(true);

		private final boolean value;

		@Override
		public boolean test(T source) throws E {
			return value;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> Predicate<T, E> alwaysFalsePredicate() {
		return (Predicate<T, E>) BooleanPredicat.ALWAYS_FALSE_PREDICATE;
	}

	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> Predicate<T, E> alwaysTruePredicate() {
		return (Predicate<T, E>) BooleanPredicat.ALWAYS_TRUE_PREDICATE;
	}

	boolean test(T source) throws E;
}
