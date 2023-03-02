package io.basc.framework.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Optional<T> extends Supplier<T> {
	public static final String NO_VALUE_PRESENT = "No value present";

	public static <U> Optional<U> empty() {
		return ofNullable(null);
	}

	public static <U> Optional<U> of(U value) {
		Assert.requiredArgument(value != null, "value");
		return new DefaultOptional<>(value);
	}

	public static <U> Optional<U> ofNullable(U value) {
		return new DefaultOptional<>(value);
	}

	public static <U> Optional<U> ofSupplier(Supplier<? extends U> valueSupplier) {
		return new StandardOptional<>(valueSupplier);
	}

	T get();

	boolean isPresent();

	default <E extends Throwable> void ifPresent(ConsumeProcessor<? super T, ? extends E> consumer) throws E {
		if (isPresent())
			consumer.process(get());
	}

	default Optional<T> ifAbsentGet(Supplier<? extends T> other) {
		Objects.requireNonNull(other);
		return ofSupplier(() -> orElse(other.get()));
	}

	default Optional<T> ifAbsent(T other) {
		return ifAbsentGet(() -> other);
	}

	default Optional<T> filter(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate);
		return ofSupplier(() -> {
			T value = orElse(null);
			if (value == null) {
				return null;
			}
			return predicate.test(value) ? null : value;
		});
	}

	default <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
		Objects.requireNonNull(mapper);
		return convert((e) -> e == null ? null : mapper.apply(e));
	}

	default <U> Optional<U> convert(Function<? super T, ? extends U> converter) {
		Objects.requireNonNull(converter);
		return ofSupplier(() -> {
			T value = orElse(null);
			return converter.apply(value);
		});
	}

	default <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
		Objects.requireNonNull(mapper);
		if (isPresent())
			return Objects.requireNonNull(mapper.apply(get()));
		else {
			return empty();
		}
	}

	default T orElse(T other) {
		return isPresent() ? get() : other;
	}

	default <E extends Throwable> T orElseGet(Source<? extends T, ? extends E> other) throws E {
		return isPresent() ? get() : other.get();
	}

	default <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		if (isPresent()) {
			return get();
		} else {
			throw exceptionSupplier.get();
		}
	}
}
