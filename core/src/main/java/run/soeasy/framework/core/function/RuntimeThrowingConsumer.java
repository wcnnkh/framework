package run.soeasy.framework.core.function;

import java.util.function.Consumer;
import java.util.function.Function;

import lombok.NonNull;

public interface RuntimeThrowingConsumer<S, E extends Throwable> extends ThrowingConsumer<S, E>, Consumer<S> {
	public static class DefaultRuntimeThrowingConsumer<S, E extends Throwable, R extends RuntimeException>
			extends MappingThrowingConsumer<S, E, S, R> implements Consumer<S> {

		public DefaultRuntimeThrowingConsumer(@NonNull ThrowingConsumer<? super S, ? extends E> compose,
				@NonNull Function<? super E, ? extends R> throwingMapper) {
			super(ThrowingFunction.identity(), compose, ThrowingConsumer.ignore(), throwingMapper,
					ThrowingConsumer.ignore());
		}

		@Override
		public String toString() {
			return getCompose().toString();
		}
	}
}
