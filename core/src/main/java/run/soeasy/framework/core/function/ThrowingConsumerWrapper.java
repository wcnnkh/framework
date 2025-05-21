package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.Wrapper;

public interface ThrowingConsumerWrapper<S, E extends Throwable, W extends ThrowingConsumer<S, E>>
		extends ThrowingConsumer<S, E>, Wrapper<W> {
	@Override
	default <R> ThrowingConsumer<R, E> map(ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
		return getSource().map(mapper);
	}

	@Override
	default ThrowingConsumer<S, E> compose(@NonNull ThrowingConsumer<? super S, ? extends E> before) {
		return getSource().compose(before);
	}

	@Override
	default ThrowingConsumer<S, E> andThen(@NonNull ThrowingConsumer<? super S, ? extends E> after) {
		return getSource().andThen(after);
	}

	@Override
	default <R extends Throwable> ThrowingConsumer<S, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return getSource().throwing(throwingMapper);
	}

	@Override
	default ThrowingConsumer<S, E> onClose(@NonNull ThrowingConsumer<? super S, ? extends E> endpoint) {
		return getSource().onClose(endpoint);
	}

	@Override
	default void accept(S source) throws E {
		getSource().accept(source);
	}
}