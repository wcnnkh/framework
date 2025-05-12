package run.soeasy.framework.core.function.runtime;

import java.util.function.Function;
import java.util.function.Predicate;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.function.ThrowingPredicate;

public interface RuntimeThrowingPredicate<S, E extends RuntimeException> extends ThrowingPredicate<S, E>, Predicate<S> {
	public static interface RuntimeThrowingPredicateWrapper<S, E extends RuntimeException, W extends RuntimeThrowingPredicate<S, E>>
			extends RuntimeThrowingPredicate<S, E>, ThrowingPredicateWrapper<S, E, W> {

		@Override
		default RuntimeThrowingPredicate<S, E> negate() {
			return getSource().negate();
		}
	}

	public static class RuntimePredicate<S, E extends Throwable, R extends RuntimeException>
			extends MergedThrowingPredicate<S, E, S, R> implements RuntimeThrowingPredicate<S, R> {

		public RuntimePredicate(@NonNull ThrowingPredicate<? super S, ? extends E> compose,
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			super(ThrowingFunction.identity(), compose, throwingMapper, ThrowingConsumer.ignore());
		}

		@Override
		public String toString() {
			return getPredicate().toString();
		}
	}

	@Override
	default RuntimeThrowingPredicate<S, E> negate() {
		return new RuntimePredicate<>(this.negate(), Function.identity());
	}
}
