package io.basc.framework.util;

import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.util.Source.FinalSource;

public final class Functions {
	private Functions() {
	}

	private static final Predicate<?> ALWAYS_TRUE_PREDICATE = (e) -> true;

	/**
	 * 永远返回true的Predicate
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> alwaysTruePredicate() {
		return (Predicate<T>) ALWAYS_TRUE_PREDICATE;
	}

	public static class FinalSupplier<T> extends FinalSource<T, RuntimeException> implements Supplier<T> {
		private static final long serialVersionUID = 1L;

		public FinalSupplier(T value) {
			super(value);
		}
	}

	public static <T> Supplier<T> forValue(T value) {
		return new FinalSupplier<T>(value);
	}

	public static <T, E extends RuntimeException> Supplier<T> forSource(Source<? extends T, ? extends E> source) {
		return null;
	}

	public static <T, E extends RuntimeException> Predicate<T> forPredicate(
			io.basc.framework.util.Predicate<? super T, ? extends E> predicate) {
		return null;
	}

	public static <S, T, E extends RuntimeException> java.util.function.Function<S, T> forFunction(
			Function<? super S, ? extends T, ? extends E> function) {
		return null;
	}

	public static <E extends RuntimeException> java.lang.Runnable forRunnable(Runnable<? extends E> runnable) {
		return null;
	}
}
