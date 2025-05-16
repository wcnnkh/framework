package run.soeasy.framework.core.function.lang;

import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.function.ThrowingFunction;

public class RuntimeConsumer<S, E extends Throwable, R extends RuntimeException>
		extends MappingThrowingConsumer<S, E, S, R> implements RuntimeThrowingConsumer<S, R> {

	public RuntimeConsumer(@NonNull ThrowingConsumer<? super S, ? extends E> compose,
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		super(ThrowingFunction.identity(), compose, ThrowingConsumer.ignore(), throwingMapper,
				ThrowingConsumer.ignore());
	}

	@Override
	public String toString() {
		return getCompose().toString();
	}
}