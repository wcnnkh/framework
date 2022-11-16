package io.basc.framework.util;

import java.util.NoSuchElementException;
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
		return new SharedOptional<>(valueSupplier);
	}

	/**
	 * If a value is present in this {@code Optional}, returns the value, otherwise
	 * throws {@code NoSuchElementException}.
	 *
	 * @return the non-null value held by this {@code Optional}
	 * @throws NoSuchElementException if there is no value present
	 *
	 * @see Optional#isPresent()
	 */
	T get();

	/**
	 * Return {@code true} if there is a value present, otherwise {@code false}.
	 *
	 * @return {@code true} if there is a value present, otherwise {@code false}
	 */
	boolean isPresent();

	/**
	 * If a value is present, invoke the specified consumer with the value,
	 * otherwise do nothing.
	 *
	 * @param consumer block to be executed if a value is present
	 * @throws NullPointerException if value is present and {@code consumer} is null
	 */
	default <E extends Throwable> void ifPresent(ConsumeProcessor<? super T, ? extends E> consumer) throws E {
		if (isPresent())
			consumer.process(get());
	}

	/**
	 * 如果不存在
	 * 
	 * @param <U>
	 * @param other
	 * @return
	 */
	default Optional<T> ifAbsentGet(Supplier<? extends T> other) {
		Objects.requireNonNull(other);
		return ofSupplier(() -> orElse(other.get()));
	}

	/**
	 * 如果不存在
	 * 
	 * @param other
	 * @return
	 */
	default Optional<T> ifAbsent(T other) {
		return ifAbsentGet(() -> other);
	}

	/**
	 * If a value is present, and the value matches the given predicate, return an
	 * {@code Optional} describing the value, otherwise return an empty
	 * {@code Optional}.
	 *
	 * @param predicate a predicate to apply to the value, if present
	 * @return an {@code Optional} describing the value of this {@code Optional} if
	 *         a value is present and the value matches the given predicate,
	 *         otherwise an empty {@code Optional}
	 * @throws NullPointerException if the predicate is null
	 */
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

	/**
	 * If a value is present, apply the provided mapping function to it, and if the
	 * result is non-null, return an {@code Optional} describing the result.
	 * Otherwise return an empty {@code Optional}.
	 *
	 * @apiNote This method supports post-processing on optional values, without the
	 *          need to explicitly check for a return status. For example, the
	 *          following code traverses a stream of file names, selects one that
	 *          has not yet been processed, and then opens that file, returning an
	 *          {@code Optional<FileInputStream>}:
	 *
	 *          <pre>{@code
	 *     Optional<FileInputStream> fis =
	 *         names.stream().filter(name -> !isProcessedYet(name))
	 *                       .findFirst()
	 *                       .map(name -> new FileInputStream(name));
	 * }</pre>
	 *
	 *          Here, {@code findFirst} returns an {@code Optional<String>}, and
	 *          then {@code map} returns an {@code Optional<FileInputStream>} for
	 *          the desired file if one exists.
	 *
	 * @param <U>    The type of the result of the mapping function
	 * @param mapper a mapping function to apply to the value, if present
	 * @return an {@code Optional} describing the result of applying a mapping
	 *         function to the value of this {@code Optional}, if a value is
	 *         present, otherwise an empty {@code Optional}
	 * @throws NullPointerException if the mapping function is null
	 */
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

	/**
	 * If a value is present, apply the provided {@code Optional}-bearing mapping
	 * function to it, return that result, otherwise return an empty
	 * {@code Optional}. This method is similar to {@link #map(Function)}, but the
	 * provided mapper is one whose result is already an {@code Optional}, and if
	 * invoked, {@code flatMap} does not wrap it with an additional
	 * {@code Optional}.
	 *
	 * @param <U>    The type parameter to the {@code Optional} returned by
	 * @param mapper a mapping function to apply to the value, if present the
	 *               mapping function
	 * @return the result of applying an {@code Optional}-bearing mapping function
	 *         to the value of this {@code Optional}, if a value is present,
	 *         otherwise an empty {@code Optional}
	 * @throws NullPointerException if the mapping function is null or returns a
	 *                              null result
	 */
	default <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
		Objects.requireNonNull(mapper);
		if (isPresent())
			return Objects.requireNonNull(mapper.apply(get()));
		else {
			return empty();
		}
	}

	/**
	 * Return the value if present, otherwise return {@code other}.
	 *
	 * @param other the value to be returned if there is no value present, may be
	 *              null
	 * @return the value, if present, otherwise {@code other}
	 */
	default T orElse(T other) {
		return isPresent() ? get() : other;
	}

	/**
	 * Return the value if present, otherwise invoke {@code other} and return the
	 * result of that invocation.
	 *
	 * @param other a {@code Supplier} whose result is returned if no value is
	 *              present
	 * @return the value if present otherwise the result of {@code other.get()}
	 * @throws NullPointerException if value is not present and {@code other} is
	 *                              null
	 */
	default <E extends Throwable> T orElseGet(Source<? extends T, ? extends E> other) throws E {
		return isPresent() ? get() : other.get();
	}

	/**
	 * Return the contained value, if present, otherwise throw an exception to be
	 * created by the provided supplier.
	 *
	 * @apiNote A method reference to the exception constructor with an empty
	 *          argument list can be used as the supplier. For example,
	 *          {@code IllegalStateException::new}
	 *
	 * @param <X>               Type of the exception to be thrown
	 * @param exceptionSupplier The supplier which will return the exception to be
	 *                          thrown
	 * @return the present value
	 * @throws X                    if there is no value present
	 * @throws NullPointerException if no value is present and
	 *                              {@code exceptionSupplier} is null
	 */
	default <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		if (isPresent()) {
			return get();
		} else {
			throw exceptionSupplier.get();
		}
	}
}
