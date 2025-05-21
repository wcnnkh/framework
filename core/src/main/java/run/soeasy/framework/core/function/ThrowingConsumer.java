package run.soeasy.framework.core.function;

import java.util.Iterator;
import java.util.function.Function;

import lombok.NonNull;

public interface ThrowingConsumer<S, E extends Throwable> {

	public static <S, E extends Throwable> void acceptAll(@NonNull Iterator<? extends S> sourceIterator,
			@NonNull ThrowingConsumer<? super S, ? extends E> consumer) throws E {
		if (sourceIterator.hasNext()) {
			try {
				consumer.accept(sourceIterator.next());
			} finally {
				acceptAll(sourceIterator, consumer);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <S, E extends Throwable> ThrowingConsumer<S, E> ignore() {
		return (ThrowingConsumer<S, E>) IgnoreThrowingConsumer.INSTANCE;
	}

	void accept(S source) throws E;

	default ThrowingConsumer<S, E> andThen(@NonNull ThrowingConsumer<? super S, ? extends E> after) {
		return new MappingThrowingConsumer<>(ThrowingFunction.identity(), this, after, Function.identity(), ignore());
	}

	default ThrowingConsumer<S, E> compose(@NonNull ThrowingConsumer<? super S, ? extends E> before) {
		return new MappingThrowingConsumer<>(ThrowingFunction.identity(), before, this, Function.identity(), ignore());
	}

	default <R> ThrowingConsumer<R, E> map(ThrowingFunction<? super R, ? extends S, ? extends E> mapper) {
		return new MappingThrowingConsumer<>(mapper, this, ignore(), Function.identity(), ignore());
	}

	default ThrowingConsumer<S, E> onClose(@NonNull ThrowingConsumer<? super S, ? extends E> endpoint) {
		return new MappingThrowingConsumer<>(ThrowingFunction.identity(), this, ignore(), Function.identity(),
				endpoint);
	}

	default <R extends Throwable> ThrowingConsumer<S, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingThrowingConsumer<>(ThrowingFunction.identity(), this, ignore(), throwingMapper, ignore());
	}
}
