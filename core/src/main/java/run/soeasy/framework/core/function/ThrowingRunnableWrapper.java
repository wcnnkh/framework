package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapper;

public interface ThrowingRunnableWrapper<E extends Throwable, W extends ThrowingRunnable<E>>
		extends ThrowingRunnable<E>, Wrapper<W> {
	@Override
	default ThrowingRunnable<E> compose(@NonNull ThrowingRunnable<? extends E> before) {
		return getSource().compose(before);
	}

	@Override
	default ThrowingRunnable<E> andThen(@NonNull ThrowingRunnable<? extends E> after) {
		return getSource().andThen(after);
	}

	@Override
	default <R extends Throwable> ThrowingRunnable<R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return getSource().throwing(throwingMapper);
	}

	@Override
	default ThrowingRunnable<E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
		return getSource().onClose(endpoint);
	}

	@Override
	default void run() throws E {
		getSource().run();
	}
}