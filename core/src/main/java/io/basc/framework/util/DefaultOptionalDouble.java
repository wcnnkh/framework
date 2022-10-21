package io.basc.framework.util;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

public final class DefaultOptionalDouble implements OptionalDouble, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Common instance for {@code empty()}.
	 */
	public static final OptionalDouble EMPTY = new DefaultOptionalDouble(false, 0);

	/**
	 * If true then the value is present, otherwise indicates no value is present
	 */
	private final boolean present;
	private final double value;

	public DefaultOptionalDouble(double value) {
		this(true, value);
	}

	public DefaultOptionalDouble(boolean present, double value) {
		this.present = present;
		this.value = value;
	}

	@Override
	public boolean isPresent() {
		return present;
	}

	@Override
	public Double get() {
		return getAsDouble();
	}

	@Override
	public double getAsDouble() {
		if (present) {
			return value;
		}
		throw new NoSuchElementException("No value present");
	}

	@Override
	public OptionalDouble filter(DoublePredicate predicate) {
		if (isPresent()) {
			return predicate.test(value) ? this : EMPTY;
		}
		return this;
	}

	@Override
	public <U> Optional<U> map(DoubleFunction<? extends U> mapper) {
		Objects.requireNonNull(mapper);
		if (present)
			return Optional.of(mapper.apply(value));
		else {
			return Optional.empty();
		}
	}

	@Override
	public <U> Optional<U> flatMap(DoubleFunction<Optional<U>> mapper) {
		Objects.requireNonNull(mapper);
		if (present) {
			return Objects.requireNonNull(mapper.apply(value));
		}
		return Optional.empty();
	}

	@Override
	public OptionalDouble filter(Predicate<? super Double> predicate) {
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
