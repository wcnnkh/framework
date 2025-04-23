package run.soeasy.framework.core.function;

import java.util.function.Function;
import java.util.function.Predicate;

import lombok.NonNull;

public interface RuntimeThrowingPredicate<S, E extends RuntimeException> extends ThrowingPredicate<S, E>, Predicate<S> {
	public static class DefaultRuntimeThrowingPredicate<S, E extends Throwable, R extends RuntimeException>
			extends MergedThrowingPredicate<S, E, S, R> implements RuntimeThrowingPredicate<S, R> {

		public DefaultRuntimeThrowingPredicate(@NonNull ThrowingPredicate<? super S, ? extends E> compose,
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
		return new DefaultRuntimeThrowingPredicate<>(this.negate(), Function.identity());
	}
}
