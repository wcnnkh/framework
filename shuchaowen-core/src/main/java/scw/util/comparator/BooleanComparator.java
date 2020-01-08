package scw.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

public class BooleanComparator implements Comparator<Boolean>, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * A shared default instance of this comparator, treating true lower than
	 * false.
	 */
	public static final BooleanComparator TRUE_LOW = new BooleanComparator(true);

	/**
	 * A shared default instance of this comparator, treating true higher than
	 * false.
	 */
	public static final BooleanComparator TRUE_HIGH = new BooleanComparator(false);

	private final boolean trueLow;

	/**
	 * Create a BooleanComparator that sorts boolean values based on the
	 * provided flag.
	 * <p>
	 * Alternatively, you can use the default shared instances:
	 * {@code BooleanComparator.TRUE_LOW} and
	 * {@code BooleanComparator.TRUE_HIGH}.
	 * 
	 * @param trueLow
	 *            whether to treat true as lower or higher than false
	 * @see #TRUE_LOW
	 * @see #TRUE_HIGH
	 */
	public BooleanComparator(boolean trueLow) {
		this.trueLow = trueLow;
	}

	public int compare(Boolean v1, Boolean v2) {
		return (v1 ^ v2) ? ((v1 ^ this.trueLow) ? 1 : -1) : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BooleanComparator)) {
			return false;
		}
		return (this.trueLow == ((BooleanComparator) obj).trueLow);
	}

	@Override
	public int hashCode() {
		return (this.trueLow ? -1 : 1) * getClass().hashCode();
	}

	@Override
	public String toString() {
		return "BooleanComparator: " + (this.trueLow ? "true low" : "true high");
	}

}
