package run.soeasy.framework.core.function.lang;

import java.util.function.Function;

import lombok.NonNull;

public class RuntimeRunnable<E extends Throwable, R extends RuntimeException>
		extends MappingThrowingRunnable<E, R> implements RuntimeThrowingRunnable<R> {

	public RuntimeRunnable(@NonNull ThrowingRunnable<? extends E> compose,
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		super(compose, ThrowingRunnable.ignore(), throwingMapper, ThrowingRunnable.ignore());
	}

	@Override
	public String toString() {
		return getCompose().toString();
	}
}