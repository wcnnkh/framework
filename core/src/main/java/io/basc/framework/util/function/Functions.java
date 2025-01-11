package io.basc.framework.util.function;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.NonNull;

public final class Functions {
	public static class FinalSupplier<T>
			extends io.basc.framework.util.function.Supplier.FinalSupplier<T, RuntimeException> implements Supplier<T> {
		private static final long serialVersionUID = 1L;

		public FinalSupplier(T value) {
			super(value);
		}
	}

	public static class ToCallable<T, E extends Exception, W extends io.basc.framework.util.function.Callable<? extends T, ? extends E>>
			extends Wrapped<W> implements Callable<T> {

		public ToCallable(W source) {
			super(source);
		}

		@Override
		public T call() throws Exception {
			return this.source.call();
		}

	}

	public static class ToFunction<S, T, E extends RuntimeException, W extends io.basc.framework.util.function.Function<? super S, ? extends T, ? extends E>>
			extends Wrapped<W> implements Function<S, T> {

		public ToFunction(W source) {
			super(source);
		}

		@Override
		public T apply(S t) {
			return this.source.apply(t);
		}
	}

	public static class ToPredicate<T, E extends RuntimeException, W extends io.basc.framework.util.function.Predicate<? super T, ? extends E>>
			extends Wrapped<W> implements Predicate<T> {

		public ToPredicate(W source) {
			super(source);
		}

		@Override
		public boolean test(T t) {
			return this.source.test(t);
		}
	}

	public static class ToRunnable<E extends RuntimeException, W extends io.basc.framework.util.function.Runnable<? extends E>>
			extends Wrapped<W> implements java.lang.Runnable {

		public ToRunnable(W source) {
			super(source);
		}

		@Override
		public void run() {
			this.source.run();
		}

	}

	public static class ToSupplier<T, E extends RuntimeException, W extends io.basc.framework.util.function.Supplier<? extends T, ? extends E>>
			extends Wrapped<W> implements Supplier<T> {

		public ToSupplier(W source) {
			super(source);
		}

		@Override
		public T get() {
			return this.source.get();
		}
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

	public static <T> Supplier<T> forValue(T value) {
		return new FinalSupplier<T>(value);
	}

	public static <T, E extends Exception> Callable<T> toCallable(
			@NonNull io.basc.framework.util.function.Callable<? extends T, ? extends E> callable) {
		return new ToCallable<>(callable);
	}

	public static <S, T, E extends RuntimeException> Function<S, T> toFunction(
			@NonNull io.basc.framework.util.function.Function<? super S, ? extends T, ? extends E> function) {
		return new ToFunction<>(function);
	}

	public static <T, E extends RuntimeException> Predicate<T> toPredicate(
			@NonNull io.basc.framework.util.function.Predicate<? super T, ? extends E> predicate) {
		return new ToPredicate<>(predicate);
	}

	public static <E extends RuntimeException> java.lang.Runnable toRunnable(@NonNull Runnable<? extends E> runnable) {
		return new ToRunnable<>(runnable);
	}

	public static <T, E extends RuntimeException> Supplier<T> toSupplier(
			@NonNull io.basc.framework.util.function.Supplier<? extends T, ? extends E> supplier) {
		return new ToSupplier<>(supplier);
	}

	private Functions() {
	}
}
