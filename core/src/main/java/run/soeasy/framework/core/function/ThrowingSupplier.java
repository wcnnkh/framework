package run.soeasy.framework.core.function;

import java.util.function.Function;

import lombok.NonNull;

public interface ThrowingSupplier<T, E extends Throwable> {

	default Pipeline<T, E> closeable() {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				Function.identity(), false, ThrowingRunnable.ignore());
	}

	default Pipeline<T, E> onClose(@NonNull ThrowingConsumer<? super T, ? extends E> consumer) {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), consumer, Function.identity(), false,
				ThrowingRunnable.ignore());
	}

	default Pipeline<T, E> onClose(@NonNull ThrowingRunnable<? extends E> closeable) {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				Function.identity(), false, closeable);
	}

	default <R> ThrowingSupplier<R, E> map(@NonNull ThrowingFunction<? super T, ? extends R, E> mapper) {
		return new MappingThrowingSupplier<>(this, mapper, ThrowingConsumer.ignore(), Function.identity(), false,
				ThrowingRunnable.ignore());
	}

	default <R extends Throwable> ThrowingSupplier<T, R> throwing(
			@NonNull Function<? super E, ? extends R> throwingMapper) {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				throwingMapper, false, ThrowingRunnable.ignore());
	}

	default Pipeline<T, E> singleton() {
		return new MappingThrowingSupplier<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				Function.identity(), true, ThrowingRunnable.ignore());
	}

	default ThrowingOptional<T, E> optional() {
		return new MappingThrowingOptional<>(this, ThrowingFunction.identity(), ThrowingConsumer.ignore(),
				Function.identity(), false, ThrowingRunnable.ignore());
	}

	T get() throws E;
}
