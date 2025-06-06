package run.soeasy.framework.core.comparator;

import java.util.Comparator;

import lombok.NonNull;

public class NullSafeComparator<T> implements Comparator<T> {

	/**
	 * A shared default instance of this comparator, treating nulls lower than
	 * non-null objects.
	 */
	@SuppressWarnings("rawtypes")
	public static final NullSafeComparator NULLS_LOW = new NullSafeComparator<Object>(true);

	/**
	 * A shared default instance of this comparator, treating nulls higher than
	 * non-null objects.
	 */
	@SuppressWarnings("rawtypes")
	public static final NullSafeComparator NULLS_HIGH = new NullSafeComparator<Object>(false);

	private final Comparator<T> nonNullComparator;

	private final boolean nullsLow;

	/**
	 * Create a NullSafeComparator that sorts {@code null} based on the provided
	 * flag, working on Comparables.
	 * <p>
	 * When comparing two non-null objects, their Comparable implementation will be
	 * used: this means that non-null elements (that this Comparator will be applied
	 * to) need to implement Comparable.
	 * <p>
	 * As a convenience, you can use the default shared instances:
	 * {@code NullSafeComparator.NULLS_LOW} and
	 * {@code NullSafeComparator.NULLS_HIGH}.
	 * 
	 * @param nullsLow whether to treat nulls lower or higher than non-null objects
	 * @see Comparable
	 * @see #NULLS_LOW
	 * @see #NULLS_HIGH
	 */
	private NullSafeComparator(boolean nullsLow) {
		this.nonNullComparator = new ComparableComparator<T>();
		this.nullsLow = nullsLow;
	}

	/**
	 * Create a NullSafeComparator that sorts {@code null} based on the provided
	 * flag, decorating the given Comparator.
	 * <p>
	 * When comparing two non-null objects, the specified Comparator will be used.
	 * The given underlying Comparator must be able to handle the elements that this
	 * Comparator will be applied to.
	 * 
	 * @param comparator the comparator to use when comparing two non-null objects
	 * @param nullsLow   whether to treat nulls lower or higher than non-null
	 *                   objects
	 */
	public NullSafeComparator(@NonNull Comparator<T> comparator, boolean nullsLow) {
		this.nonNullComparator = comparator;
		this.nullsLow = nullsLow;
	}

	public int compare(T o1, T o2) {
		if (o1 == o2) {
			return 0;
		}
		if (o1 == null) {
			return (this.nullsLow ? -1 : 1);
		}
		if (o2 == null) {
			return (this.nullsLow ? 1 : -1);
		}
		return this.nonNullComparator.compare(o1, o2);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NullSafeComparator)) {
			return false;
		}
		NullSafeComparator<T> other = (NullSafeComparator<T>) obj;
		return (this.nonNullComparator.equals(other.nonNullComparator) && this.nullsLow == other.nullsLow);
	}

	@Override
	public int hashCode() {
		return (this.nullsLow ? -1 : 1) * this.nonNullComparator.hashCode();
	}

	@Override
	public String toString() {
		return "NullSafeComparator: non-null comparator [" + this.nonNullComparator + "]; "
				+ (this.nullsLow ? "nulls low" : "nulls high");
	}

}
