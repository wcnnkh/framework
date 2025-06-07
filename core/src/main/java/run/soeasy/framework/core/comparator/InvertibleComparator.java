package run.soeasy.framework.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

import lombok.NonNull;

public class InvertibleComparator<T> implements Comparator<T>, Serializable {
	private static final long serialVersionUID = 1L;

	private final Comparator<T> comparator;

	private boolean ascending = true;

	/**
	 * Create an InvertibleComparator that sorts ascending by default. For the
	 * actual comparison, the specified Comparator will be used.
	 * 
	 * @param comparator the comparator to decorate
	 */
	public InvertibleComparator(@NonNull Comparator<T> comparator) {
		this.comparator = comparator;
	}

	/**
	 * Create an InvertibleComparator that sorts based on the provided order. For
	 * the actual comparison, the specified Comparator will be used.
	 * 
	 * @param comparator the comparator to decorate
	 * @param ascending  the sort order: ascending (true) or descending (false)
	 */
	public InvertibleComparator(@NonNull Comparator<T> comparator, boolean ascending) {
		this.comparator = comparator;
		setAscending(ascending);
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isAscending() {
		return this.ascending;
	}

	/**
	 * Invert the sort order: ascending --&gt; descending or descending --&gt;
	 * ascending.
	 */
	public void invertOrder() {
		this.ascending = !this.ascending;
	}

	public int compare(T o1, T o2) {
		int result = this.comparator.compare(o1, o2);
		if (result != 0) {
			// Invert the order if it is a reverse sort.
			if (!this.ascending) {
				if (Integer.MIN_VALUE == result) {
					result = Integer.MAX_VALUE;
				} else {
					result *= -1;
				}
			}
			return result;
		}
		return 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof InvertibleComparator)) {
			return false;
		}
		InvertibleComparator<T> other = (InvertibleComparator<T>) obj;
		return (this.comparator.equals(other.comparator) && this.ascending == other.ascending);
	}

	@Override
	public int hashCode() {
		return this.comparator.hashCode();
	}

	@Override
	public String toString() {
		return "InvertibleComparator: [" + this.comparator + "]; ascending=" + this.ascending;
	}
}
