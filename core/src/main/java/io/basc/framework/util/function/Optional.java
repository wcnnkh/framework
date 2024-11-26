package io.basc.framework.util.function;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.util.Endpoint;
import io.basc.framework.util.Pipeline;
import io.basc.framework.util.Source;
import lombok.NonNull;

@FunctionalInterface
public interface Optional<T> extends Source<T, RuntimeException> {

	@SuppressWarnings("unchecked")
	public static <U> Optional<U> empty() {
		return (Optional<U>) DefaultOptional.EMPTY;
	}

	public static <U> Optional<U> of(U value) {
		if (value == null) {
			return empty();
		}
		return new DefaultOptional<>(value);
	}

	public static <U> Optional<U> ofSupplier(Supplier<? extends U> valueSupplier) {
		return new StandardOptional<>(valueSupplier);
	}

	/**
	 * 调用{@link #map(Function)}
	 * 
	 * @param predicate
	 * @return
	 */
	default Optional<T> filter(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate);
		return map((value) -> predicate.test(value) ? null : value);
	}

	default <U> Optional<U> flatMap(Pipeline<? super T, ? extends Optional<U>, ? extends RuntimeException> mapper) {
		Objects.requireNonNull(mapper);
		T value = orElse(null);
		if (value == null) {
			return empty();
		}
		return Objects.requireNonNull(mapper.apply(value));
	}

	default T get() throws NoSuchElementException {
		T value = orElse(null);
		if (value == null) {
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	/**
	 * 调用{@link #ifAbsentGet(Supplier)}
	 * 
	 * @param other
	 * @return
	 */
	default Optional<T> ifAbsent(T other) {
		return ifAbsentGet(() -> other);
	}

	/**
	 * 如果不存在
	 * 
	 * @param other
	 * @return
	 */
	default Optional<T> ifAbsentGet(Supplier<? extends T> other) {
		Objects.requireNonNull(other);
		T value = orElse(null);
		if (value == null) {
			return ofSupplier(other);
		}
		return this;
	}

	default <E extends Throwable> void ifPresent(Endpoint<? super T, ? extends E> consumer) throws E {
		T value = orElse(null);
		if (value != null) {
			consumer.accept(value);
		}
	}

	default boolean isPresent() {
		return orElse(null) != null;
	}

	@Override
	default <R> Optional<R> map(@NonNull Pipeline<? super T, ? extends R, ? extends RuntimeException> mapper) {
		Objects.requireNonNull(mapper);
		return flatMap((e) -> ofSupplier(() -> mapper.apply(e)));
	}

	/**
	 * 为了保证一些实现的原子性，默认不实现此方法
	 * 
	 * @param other
	 * @return
	 */
	T orElse(T other);

	default <E extends Throwable> T orElseGet(Source<? extends T, ? extends E> other) throws E {
		T value = orElse(null);
		return value == null ? other.get() : value;
	}

	default <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		T value = orElse(null);
		if (value == null) {
			throw exceptionSupplier.get();
		}
		return value;
	}
}
