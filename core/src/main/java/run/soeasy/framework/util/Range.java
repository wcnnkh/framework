package run.soeasy.framework.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.util.collections.Elements;

public final class Range<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private final static Range<?> UNBOUNDED = Range.of(Bound.unbounded(), Bound.unbounded());

	/**
	 * The lower bound of the range.
	 */
	private final Bound<T> lowerBound;

	/**
	 * The upper bound of the range.
	 */
	private final Bound<T> upperBound;

	public Range(@NonNull Bound<T> lowerBound, @NonNull Bound<T> upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public <U, E> Range<U> convert(Function<? super T, ? extends U> converter) {
		return new Range<U>(lowerBound.convert(converter), upperBound.convert(converter));
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
	 */
	@SuppressWarnings("unchecked")
	public static <T> Range<T> unbounded() {
		return (Range<T>) UNBOUNDED;
	}

	/**
	 * Creates a new {@link Range} with inclusive bounds for both values.
	 *
	 * @param <T>
	 * @param from must not be {@literal null}.
	 * @param to   must not be {@literal null}.
	 * @return
	 */
	public static <T> Range<T> closed(T from, T to) {
		return new Range<>(Bound.inclusive(from), Bound.inclusive(to));
	}

	/**
	 * Creates a new {@link Range} with exclusive bounds for both values.
	 *
	 * @param <T>
	 * @param from must not be {@literal null}.
	 * @param to   must not be {@literal null}.
	 * @return
	 */
	public static <T> Range<T> open(T from, T to) {
		return new Range<>(Bound.exclusive(from), Bound.exclusive(to));
	}

	/**
	 * Creates a new left-open {@link Range}, i.e. left exclusive, right inclusive.
	 *
	 * @param <T>
	 * @param from must not be {@literal null}.
	 * @param to   must not be {@literal null}.
	 * @return
	 */
	public static <T> Range<T> leftOpen(T from, T to) {
		return new Range<>(Bound.exclusive(from), Bound.inclusive(to));
	}

	/**
	 * Creates a new right-open {@link Range}, i.e. left inclusive, right exclusive.
	 *
	 * @param <T>
	 * @param from must not be {@literal null}.
	 * @param to   must not be {@literal null}.
	 * @return
	 */
	public static <T> Range<T> rightOpen(T from, T to) {
		return new Range<>(Bound.inclusive(from), Bound.exclusive(to));
	}

	/**
	 * Creates a left-unbounded {@link Range} (the left bound set to
	 * {@link Bound#unbounded()}) with the given right bound.
	 *
	 * @param <T>
	 * @param to  the right {@link Bound}, must not be {@literal null}.
	 * @return
	 */
	public static <T> Range<T> leftUnbounded(Bound<T> to) {
		return new Range<>(Bound.unbounded(), to);
	}

	/**
	 * Creates a right-unbounded {@link Range} (the right bound set to
	 * {@link Bound#unbounded()}) with the given left bound.
	 *
	 * @param <T>
	 * @param from the left {@link Bound}, must not be {@literal null}.
	 * @return
	 */
	public static <T> Range<T> rightUnbounded(Bound<T> from) {
		return new Range<>(from, Bound.unbounded());
	}

	/**
	 * Create a {@link RangeBuilder} given the lower {@link Bound}.
	 *
	 * @param lower must not be {@literal null}.
	 * @return
	 */
	public static <T> RangeBuilder<T> from(Bound<T> lower) {

		Assert.notNull(lower, "Lower bound must not be null!");
		return new RangeBuilder<>(lower);
	}

	/**
	 * Creates a new {@link Range} with the given lower and upper bound. Prefer
	 * {@link #from(Bound)} for a more builder style API.
	 *
	 * @param lowerBound must not be {@literal null}.
	 * @param upperBound must not be {@literal null}.
	 * @see #from(Bound)
	 */
	public static <T> Range<T> of(Bound<T> lowerBound, Bound<T> upperBound) {
		return new Range<>(lowerBound, upperBound);
	}

	/**
	 * Creates a new Range with the given value as sole member.
	 *
	 * @param <T>
	 * @param value must not be {@literal null}.
	 * @return
	 */
	public static <T> Range<T> just(T value) {
		return Range.closed(value, value);
	}

	/**
	 * Returns whether the {@link Range} contains the given value.
	 * 
	 * @param value      value must not be {@literal null}.
	 * @param comparator
	 * @return
	 */
	public boolean contains(@NonNull T value, @NonNull Comparator<T> comparator) {
		return lowerBound.rightContains(value, comparator) && upperBound.leftContains(value, comparator);
	}

	/**
	 * 将多个范围合并成一个范围
	 * 
	 * @param <R>
	 * @param elements
	 * @param comparator
	 * @return
	 */
	public static <R> Range<R> unionAll(@NonNull Elements<? extends Range<R>> elements,
			@NonNull Comparator<R> comparator) {
		Assert.isTrue(!elements.isEmpty(), "element cannot be empty");
		Bound<R> lower = elements.map((e) -> e.getLowerBound()).sorted((b1, b2) -> b1.compare(b2, comparator)).first();
		Bound<R> upper = elements.map((e) -> e.getUpperBound()).sorted((b1, b2) -> b1.compare(b2, comparator)).last();
		return new Range<>(lower, upper);
	}

	/**
	 * 将两个范围组合
	 * 
	 * @param range
	 * @param comparator
	 * @return
	 */
	public Range<T> union(@NonNull Range<T> range, @NonNull Comparator<T> comparator) {
		return unionAll(Elements.forArray(this, range), comparator);
	}

	public boolean contains(@NonNull Range<T> range, @NonNull Comparator<T> comparator) {
		if (lowerBound.isBounded()) {
			if (range.getLowerBound().isBounded()) {
				// 都有边界
				if (!lowerBound.rightContains(range.getLowerBound().get(), comparator)) {
					return false;
				}
			} else {
				// 在一个有边界的范围里无边界的数据不会是他的子集
				return false;
			}
		}

		if (upperBound.isBounded()) {
			if (range.getUpperBound().isBounded()) {
				// 都有边界
				if (!lowerBound.leftContains(range.getUpperBound().get(), comparator)) {
					return false;
				}
			} else {
				// 在一个有边界的范围里无边界的数据不会是他的子集
				return false;
			}
		}
		return true;
	}

	String toPrefixString() {
		return lowerBound.map(Object::toString) //
				.map(it -> lowerBound.isInclusive() ? "[".concat(it) : "(".concat(it)) //
				.orElse("unbounded");
	}

	String toSuffixString() {
		return upperBound.map(Object::toString) //
				.map(it -> upperBound.isInclusive() ? it.concat("]") : it.concat(")")) //
				.orElse("unbounded");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s-%s", toPrefixString(), toSuffixString());
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] { lowerBound, upperBound });
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Range) {
			Range<?> other = (Range<?>) obj;
			return ObjectUtils.equals(lowerBound, other.lowerBound) && ObjectUtils.equals(upperBound, other.upperBound);
		}
		return false;
	}

	/**
	 * Builder for {@link Range} allowing to specify the upper boundary.
	 *
	 */
	public static class RangeBuilder<T> {

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