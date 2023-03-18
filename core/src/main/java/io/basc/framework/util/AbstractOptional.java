package io.basc.framework.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;

public abstract class AbstractOptional<T> implements Optional<T> {

	@Nullable
	protected abstract T getValue();

	@Override
	public T get() {
		T value = getValue();
		if (value == null) {
			throw noSuchElement();
		}
		return value;
	}

	protected RuntimeException noSuchElement() {
		return new NoSuchElementException("No value present");
	}

	@Override
	public boolean isPresent() {
		return getValue() != null;
	}

	@Override
	public T orElse(T other) {
		T value = getValue();
		return value == null ? other : value;
	}

	@Override
	public <E extends Throwable> T orElseGet(Source<? extends T, ? extends E> other) throws E {
		T value = getValue();
		return value == null ? other.get() : value;
	}

	@Override
	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		T value = getValue();
		if (value != null) {
			return value;
		} else {
			throw exceptionSupplier.get();
		}
	}

	@Override
	public <E extends Throwable> void ifPresent(ConsumeProcessor<? super T, ? extends E> consumer) throws E {
		T value = getValue();
		if (value != null)
			consumer.process(value);
	}

	@Override
	public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
		Objects.requireNonNull(mapper);
		T value = getValue();
		if (value == null)
			return Optional.empty();
		else {
			return Objects.requireNonNull(mapper.apply(value));
		}
	}

	/**
	 * Indicates whether some other object is "equal to" this Optional. The other
	 * object is considered equal if:
	 * <ul>
	 * <li>it is also an {@code Optional} and;
	 * <li>both instances have no value present or;
	 * <li>the present values are "equal to" each other via {@code equals()}.
	 * </ul>
	 *
	 * @param obj an object to be tested for equality
	 * @return {code true} if the other object is "equal to" this object otherwise
	 *         {@code false}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof AbstractOptional)) {
			return false;
		}

		AbstractOptional<?> other = (AbstractOptional<?>) obj;
		return Objects.equals(getValue(), other.getValue());
	}

	/**
	 * Returns the hash code value of the present value, if any, or 0 (zero) if no
	 * value is present.
	 *
	 * @return hash code value of the present value or 0 if no value is present
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(getValue());
	}

	@Override
	public String toString() {
		T value = getValue();
		return isPresent() ? String.format("Optional[%s]", value) : "Optional.empty";
	}
}
