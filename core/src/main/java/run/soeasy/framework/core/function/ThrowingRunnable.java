package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

public interface ThrowingRunnable<E extends Throwable> {
	@SuppressWarnings("unchecked")
	public static <E extends Throwable> ThrowingRunnable<E> ignore() {
		return (IgnoreThrowingRunnable<E>) IgnoreThrowingRunnable.INSTANCE;
	}

	default ThrowingRunnable<E> compose(@NonNull ThrowingRunnable<? extends E> before) {
		return new MappingThrowingRunnable<>(before, this, Function.identity(), ignore());
	}

	default ThrowingRunnable<E> andThen(@NonNull ThrowingRunnable<? extends E> after) {
		return new MappingThrowingRunnable<>(this, after, Function.identity(), ignore());
	}

	default <R extends Throwable> ThrowingRunnable<R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingThrowingRunnable<>(this, ignore(), throwingMapper, ignore());
	}

	default ThrowingRunnable<E> onClose(@NonNull ThrowingRunnable<? extends E> endpoint) {
		return new MappingThrowingRunnable<>(this, ignore(), Function.identity(), endpoint);
	}

	void run() throws E;
}
