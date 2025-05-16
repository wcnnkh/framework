package run.soeasy.framework.core.function.lang;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface ThrowingPredicate<S, E extends Throwable> {

	@SuppressWarnings("unchecked")
	public static <S, E extends Throwable> ThrowingPredicate<S, E> alwaysFalse() {
		return (ThrowingPredicate<S, E>) AlwaysBooleanPredicat.FALSE;
	}

	@SuppressWarnings("unchecked")
	public static <S, E extends Throwable> ThrowingPredicate<S, E> alwaysTrue() {
		return (ThrowingPredicate<S, E>) AlwaysBooleanPredicat.TRUE;
	}


	default <R> ThrowingPredicate<R, E> map(@NonNull ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
		return new MergedThrowingPredicate<>(mapper, this, Function.identity(), ThrowingConsumer.ignore());
	}

	default ThrowingPredicate<S, E> and(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
		return (t) -> test(t) && other.test(t);
	}

	default ThrowingPredicate<S, E> negate() {
		return (t) -> !test(t);
	}

	default ThrowingPredicate<S, E> or(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
		return (t) -> test(t) || other.test(t);
	}

	default <R extends Throwable> ThrowingPredicate<S, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MergedThrowingPredicate<>(ThrowingFunction.identity(), this, throwingMapper,
				ThrowingConsumer.ignore());
	}

	default <R extends RuntimeException> RuntimeThrowingPredicate<S, R> runtime(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new RuntimePredicate<>(this, throwingMapper);
	}

	default RuntimeThrowingPredicate<S, RuntimeException> runtime() {
		return runtime((e) -> e instanceof RuntimeException ? ((RuntimeException) e) : new RuntimeException(e));
	}

	boolean test(S source) throws E;
}
