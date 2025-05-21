package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.Wrapper;

public interface ThrowingPredicateWrapper<S, E extends Throwable, W extends ThrowingPredicate<S, E>>
		extends ThrowingPredicate<S, E>, Wrapper<W> {
	@Override
	default <R> ThrowingPredicate<R, E> map(@NonNull ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
		return getSource().map(mapper);
	}

	@Override
	default ThrowingPredicate<S, E> and(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
		return getSource().and(other);
	}

	@Override
	default ThrowingPredicate<S, E> negate() {
		return getSource().negate();
	}

	@Override
	default ThrowingPredicate<S, E> or(@NonNull ThrowingPredicate<? super S, ? extends E> other) {
		return getSource().or(other);
	}

	@Override
	default <R extends Throwable> ThrowingPredicate<S, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return getSource().throwing(throwingMapper);
	}

	@Override
	default boolean test(S source) throws E {
		return getSource().test(source);
	}
}