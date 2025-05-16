package run.soeasy.framework.core.function.lang;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

public class RuntimePredicate<S, E extends Throwable, R extends RuntimeException>
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