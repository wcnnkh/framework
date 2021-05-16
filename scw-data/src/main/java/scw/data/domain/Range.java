package scw.data.domain;

import java.util.Optional;

import scw.core.Assert;

public class Range<T extends Comparable<T>> {

	private final static Range<?> UNBOUNDED = Range.of(Bound.unbounded(), Bound.UNBOUNDED);

	/**
	 * The lower bound of the range.
	 */
	private final Bound<T> lowerBound;

	/**
	 * The upper bound of the range.
	 */
	private final Bound<T> upperBound;

	public Range(Bound<T> lowerBound, Bound<T> upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public Bound<T> getLowerBound() {
		return lowerBound;
	}

	public Bound<T> getUpperBound() {
		return upperBound;
	}

	/**
	 * Returns an unbounded {@link Range}.
	 *
	 * @return
	 * @since 2.0
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<T>> Range<T> unbounded() {
		return (Range<T>) UNBOUNDED;
	}

	/**
	 * Creates a new {@link Range} with inclusive bounds for both values.
	 *
	 * @param <T>
	 * @param from must not be {@literal null}.
	 * @param to   must not be {@literal null}.
	 * @return
	 * @since 2.2
	 */
	public static <T extends Comparable<T>> Range<T> closed(T from, T to) {
		return new Range<>(Bound.inclusive(from), Bound.inclusive(to));
	}

	/**
	 * Creates a new {@link Range} with exclusive bounds for both values.
	 *
	 * @param <T>
	 * @param from must not be {@literal null}.
	 * @param to   must not be {@literal null}.
	 * @return
	 * @since 2.2
	 */
	public static <T extends Comparable<T>> Range<T> open(T from, T to) {
		return new Range<>(Bound.exclusive(from), Bound.exclusive(to));
	}

	/**
	 * Creates a new left-open {@link Range}, i.e. left exclusive, right inclusive.
	 *
	 * @param <T>
	 * @param from must not be {@literal null}.
	 * @param to   must not be {@literal null}.
	 * @return
	 * @since 2.2
	 */
	public static <T extends Comparable<T>> Range<T> leftOpen(T from, T to) {
		return new Range<>(Bound.exclusive(from), Bound.inclusive(to));
	}

	/**
	 * Creates a new right-open {@link Range}, i.e. left inclusive, right exclusive.
	 *
	 * @param <T>
	 * @param from must not be {@literal null}.
	 * @param to   must not be {@literal null}.
	 * @return
	 * @since 2.2
	 */
	public static <T extends Comparable<T>> Range<T> rightOpen(T from, T to) {
		return new Range<>(Bound.inclusive(from), Bound.exclusive(to));
	}

	/**
	 * Creates a left-unbounded {@link Range} (the left bound set to
	 * {@link Bound#unbounded()}) with the given right bound.
	 *
	 * @param <T>
	 * @param to  the right {@link Bound}, must not be {@literal null}.
	 * @return
	 * @since 2.2
	 */
	public static <T extends Comparable<T>> Range<T> leftUnbounded(Bound<T> to) {
		return new Range<>(Bound.unbounded(), to);
	}

	/**
	 * Creates a right-unbounded {@link Range} (the right bound set to
	 * {@link Bound#unbounded()}) with the given left bound.
	 *
	 * @param <T>
	 * @param from the left {@link Bound}, must not be {@literal null}.
	 * @return
	 * @since 2.2
	 */
	public static <T extends Comparable<T>> Range<T> rightUnbounded(Bound<T> from) {
		return new Range<>(from, Bound.unbounded());
	}

	/**
	 * Create a {@link RangeBuilder} given the lower {@link Bound}.
	 *
	 * @param lower must not be {@literal null}.
	 * @return
	 * @since 2.0
	 */
	public static <T extends Comparable<T>> RangeBuilder<T> from(Bound<T> lower) {

		Assert.notNull(lower, "Lower bound must not be null!");
		return new RangeBuilder<>(lower);
	}

	/**
	 * Creates a new {@link Range} with the given lower and upper bound. Prefer
	 * {@link #from(Bound)} for a more builder style API.
	 *
	 * @param lowerBound must not be {@literal null}.
	 * @param upperBound must not be {@literal null}.
	 * @since 2.0
	 * @see #from(Bound)
	 */
	public static <T extends Comparable<T>> Range<T> of(Bound<T> lowerBound, Bound<T> upperBound) {
		return new Range<>(lowerBound, upperBound);
	}

	/**
	 * Creates a new Range with the given value as sole member.
	 *
	 * @param <T>
	 * @param value must not be {@literal null}.
	 * @return
	 * @see Range#closed(Comparable, Comparable)
	 */
	public static <T extends Comparable<T>> Range<T> just(T value) {
		return Range.closed(value, value);
	}

	/**
	 * Returns whether the {@link Range} contains the given value.
	 *
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public boolean contains(T value) {

		Assert.notNull(value, "Reference value must not be null!");

		boolean greaterThanLowerBound = lowerBound.getValue() //
				.map(it -> lowerBound.isInclusive() ? it.compareTo(value) <= 0 : it.compareTo(value) < 0) //
				.orElse(true);

		boolean lessThanUpperBound = upperBound.getValue() //
				.map(it -> upperBound.isInclusive() ? it.compareTo(value) >= 0 : it.compareTo(value) > 0) //
				.orElse(true);

		return greaterThanLowerBound && lessThanUpperBound;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s-%s", lowerBound.toPrefixString(), upperBound.toSuffixString());
	}

	/**
	 * Value object representing a boundary. A boundary can either be
	 * {@link #unbounded() unbounded}, {@link #inclusive(Comparable) including its
	 * value} or {@link #exclusive(Comparable) its value}.
	 *
	 * @author Mark Paluch
	 * @since 2.0
	 * @soundtrack Mohamed Ragab - Excelsior Sessions (March 2017)
	 */
	public static class Bound<T extends Comparable<T>> {

		@SuppressWarnings({ "rawtypes", "unchecked" }) //
		private static final Bound<?> UNBOUNDED = new Bound(Optional.empty(), true);

		private final Optional<T> value;
		private final boolean inclusive;

		private Bound(Optional<T> value, boolean inclusive) {
			this.value = value;
			this.inclusive = inclusive;
		}

		public Optional<T> getValue() {
			return value;
		}

		public boolean isInclusive() {
			return inclusive;
		}

		/**
		 * Creates an unbounded {@link Bound}.
		 */
		@SuppressWarnings("unchecked")
		public static <T extends Comparable<T>> Bound<T> unbounded() {
			return (Bound<T>) UNBOUNDED;
		}

		/**
		 * Returns whether this boundary is bounded.
		 *
		 * @return
		 */
		public boolean isBounded() {
			return value.isPresent();
		}

		/**
		 * Creates a boundary including {@code value}.
		 *
		 * @param value must not be {@literal null}.
		 * @return
		 */
		public static <T extends Comparable<T>> Bound<T> inclusive(T value) {

			Assert.notNull(value, "Value must not be null!");
			return new Bound<>(Optional.of(value), true);
		}

		/**
		 * Creates a boundary including {@code value}.
		 *
		 * @param value must not be {@literal null}.
		 * @return
		 */
		public static Bound<Integer> inclusive(int value) {
			return inclusive((Integer) value);
		}

		/**
		 * Creates a boundary including {@code value}.
		 *
		 * @param value must not be {@literal null}.
		 * @return
		 */
		public static Bound<Long> inclusive(long value) {
			return inclusive((Long) value);
		}

		/**
		 * Creates a boundary including {@code value}.
		 *
		 * @param value must not be {@literal null}.
		 * @return
		 */
		public static Bound<Float> inclusive(float value) {
			return inclusive((Float) value);
		}

		/**
		 * Creates a boundary including {@code value}.
		 *
		 * @param value must not be {@literal null}.
		 * @return
		 */
		public static Bound<Double> inclusive(double value) {
			return inclusive((Double) value);
		}

		/**
		 * Creates a boundary excluding {@code value}.
		 *
		 * @param value must not be {@literal null}.
		 * @return
		 */
		public static <T extends Comparable<T>> Bound<T> exclusive(T value) {

			Assert.notNull(value, "Value must not be null!");
			return new Bound<>(Optional.of(value), false);
		}

		/**
		 * Creates a boundary excluding {@code value}.
		 *
		 * @param value must not be {@literal null}.
		 * @return
		 */
		public static Bound<Integer> exclusive(int value) {
			return exclusive((Integer) value);
		}

		/**
		 * Creates a boundary excluding {@code value}.
		 *
		 * @param value must not be {@literal null}.
		 * @return
		 */
		public static Bound<Long> exclusive(long value) {
			return exclusive((Long) value);
		}

		/**
		 * Creates a boundary excluding {@code value}.
		 *
		 * @param value must not be {@literal null}.
		 * @return
		 */
		public static Bound<Float> exclusive(float value) {
			return exclusive((Float) value);
		}

		/**
		 * Creates a boundary excluding {@code value}.
		 *
		 * @param value must not be {@literal null}.
		 * @return
		 */
		public static Bound<Double> exclusive(double value) {
			return exclusive((Double) value);
		}

		String toPrefixString() {

			return getValue() //
					.map(Object::toString) //
					.map(it -> isInclusive() ? "[".concat(it) : "(".concat(it)) //
					.orElse("unbounded");
		}

		String toSuffixString() {

			return getValue() //
					.map(Object::toString) //
					.map(it -> isInclusive() ? it.concat("]") : it.concat(")")) //
					.orElse("unbounded");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return value.map(Object::toString).orElse("unbounded");
		}

	}

	/**
	 * Builder for {@link Range} allowing to specify the upper boundary.
	 *
	 * @soundtrack Aly and Fila - Future Sound Of Egypt 493
	 */
	public static class RangeBuilder<T extends Comparable<T>> {

		private final Bound<T> lower;

		RangeBuilder(Bound<T> lower) {
			this.lower = lower;
		}

		/**
		 * Create a {@link Range} given the upper {@link Bound}.
		 *
		 * @param upper must not be {@literal null}.
		 * @return
		 */
		public Range<T> to(Bound<T> upper) {

			Assert.notNull(upper, "Upper bound must not be null!");
			return new Range<>(lower, upper);
		}
	}
}