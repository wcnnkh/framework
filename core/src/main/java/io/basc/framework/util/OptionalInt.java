package io.basc.framework.util;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

import io.basc.framework.util.stream.IntConsumerProcessor;

public interface OptionalInt extends Optional<Integer>, IntSupplier {

	static OptionalInt empty() {
		return DefaultOptionalInt.EMPTY;
	}

	static OptionalInt of(int value) {
		return new DefaultOptionalInt(value);
	}

	/**
	 * Have the specified consumer accept the value if a value is present, otherwise
	 * do nothing.
	 *
	 * @param consumer block to be executed if a value is present
	 * @throws NullPointerException if value is present and {@code consumer} is null
	 */
	default <E extends Throwable> void ifPresent(IntConsumerProcessor<? extends E> consumer) throws E {
		if (isPresent()) {
			consumer.process(getAsInt());
		}
	}

	/**
	 * Return the value if present, otherwise return {@code other}.
	 *
	 * @param other the value to be returned if there is no value present
	 * @return the value, if present, otherwise {@code other}
	 */
	default int orElse(int other) {
		if (isPresent()) {
			return getAsInt();
		}
		return other;
	}

	@Override
	OptionalInt filter(Predicate<? super Integer> predicate);

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
	OptionalInt filter(IntPredicate predicate);

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
	<U> Optional<U> map(IntFunction<? extends U> mapper);

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
	<U> Optional<U> flatMap(IntFunction<Optional<U>> mapper);
}
