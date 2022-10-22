package io.basc.framework.util;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

public final class DefaultOptionalLong implements OptionalLong, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Common instance for {@code empty()}.
	 */
	public static final OptionalLong EMPTY = new DefaultOptionalLong(false, 0);

	/**
	 * If true then the value is present, otherwise indicates no value is present
	 */
	private final boolean present;
	private final long value;

	public DefaultOptionalLong(long value) {
		this(true, value);
	}

	public DefaultOptionalLong(boolean present, long value) {
		this.present = present;
		this.value = value;
	}

	@Override
	public boolean isPresent() {
		return present;
	}

	@Override
	public Long get() {
		return getAsLong();
	}

	@Override
	public long getAsLong() {
		if (present) {
			return value;
		}
		throw new NoSuchElementException("No value present");
	}

	@Override
	public OptionalLong filter(LongPredicate predicate) {
		if (isPresent()) {
			return predicate.test(value) ? this : EMPTY;
		}
		return this;
	}

	@Override
	public <U> Optional<U> map(LongFunction<? extends U> mapper) {
		Objects.requireNonNull(mapper);
		if (present)
			return Optional.of(mapper.apply(value));
		else {
			return Optional.empty();
		}
	}

	@Override
	public <U> Optional<U> flatMap(LongFunction<Optional<U>> mapper) {
		Objects.requireNonNull(mapper);
		if (present) {
			return Objects.requireNonNull(mapper.apply(value));
		}
		return Optional.empty();
	}

	@Override
	public OptionalLong filter(Predicate<? super Long> predicate) {
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
