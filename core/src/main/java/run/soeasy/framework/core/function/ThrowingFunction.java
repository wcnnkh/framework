package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

public interface ThrowingFunction<S, T, E extends Throwable> {

	@SuppressWarnings("unchecked")
	public static <T, E extends Throwable> ThrowingFunction<T, T, E> identity() {
		return (ThrowingFunction<T, T, E>) IdentityThrowingFunction.INSTANCE;
	}

	default <R> ThrowingFunction<R, T, E> compose(
			@NonNull ThrowingFunction<? super R, ? extends S, ? extends E> before) {
		return new MappingThrowingFunction<>(before, this, Function.identity(), ThrowingConsumer.ignore());
	}

	default <R> ThrowingFunction<S, R, E> andThen(
			@NonNull ThrowingFunction<? super T, ? extends R, ? extends E> after) {
		return new MappingThrowingFunction<>(this, after, Function.identity(), ThrowingConsumer.ignore());
	}

	default <R extends Throwable> ThrowingFunction<S, T, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingThrowingFunction<>(this, identity(), throwingMapper, ThrowingConsumer.ignore());
	}

	default ThrowingFunction<S, T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> endpoint) {
		return new MappingThrowingFunction<>(this, identity(), Function.identity(), endpoint);
	}

	T apply(S source) throws E;
}
