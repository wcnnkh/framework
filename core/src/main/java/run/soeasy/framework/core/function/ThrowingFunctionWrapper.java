package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

public interface ThrowingFunctionWrapper<S, T, E extends Throwable, W extends ThrowingFunction<S, T, E>>
		extends ThrowingFunction<S, T, E>, Wrapper<W> {
	@Override
	default <R> ThrowingFunction<R, T, E> compose(
			@NonNull ThrowingFunction<? super R, ? extends S, ? extends E> before) {
		return getSource().compose(before);
	}

	@Override
	default <R> ThrowingFunction<S, R, E> andThen(
			@NonNull ThrowingFunction<? super T, ? extends R, ? extends E> after) {
		return getSource().andThen(after);
	}

	@Override
	default <R extends Throwable> ThrowingFunction<S, T, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return getSource().throwing(throwingMapper);
	}

	@Override
	default ThrowingFunction<S, T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
		return getSource().onClose(endpoint);
	}

	@Override
	default T apply(S source) throws E {
		return getSource().apply(source);
	}
}
