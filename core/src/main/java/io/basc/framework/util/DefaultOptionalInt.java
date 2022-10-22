package io.basc.framework.util;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

public final class DefaultOptionalInt implements OptionalInt, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Common instance for {@code empty()}.
	 */
	public static final OptionalInt EMPTY = new DefaultOptionalInt(false, 0);

	/**
	 * If true then the value is present, otherwise indicates no value is present
	 */
	private final boolean present;
	private final int value;

	public DefaultOptionalInt(int value) {
		this(true, value);
	}

	public DefaultOptionalInt(boolean present, int value) {
		this.present = present;
		this.value = value;
	}

	@Override
	public boolean isPresent() {
		return present;
	}

	@Override
	public Integer get() {
		return getAsInt();
	}

	@Override
	public int getAsInt() {
		if (isPresent()) {
			return value;
		}
		throw new NoSuchElementException("No value present");
	}

	@Override
	public OptionalInt filter(IntPredicate predicate) {
		if (isPresent()) {
			return predicate.test(value) ? this : EMPTY;
		}
		return this;
	}

	@Override
	public <U> Optional<U> map(IntFunction<? extends U> mapper) {
		Objects.requireNonNull(mapper);
		if (present)
			return Optional.of(mapper.apply(value));
		else {
			return Optional.empty();
		}
	}

	@Override
	public <U> Optional<U> flatMap(IntFunction<Optional<U>> mapper) {
		Objects.requireNonNull(mapper);
		if (present) {
			return Objects.requireNonNull(mapper.apply(value));
		}
		return Optional.empty();
	}

	@Override
	public OptionalInt filter(Predicate<? super Integer> predicate) {
		if (isPresent()) {
			return predicate.test(value) ? this : EMPTY;
		}
		return this;
	}

	@Override
	public String toString() {
		return present ? String.format("OptionalInt[%s]", value) : "OptionalInt.empty";
	}
}
