package run.soeasy.framework.core.comparator;

/**
 * {@code Ordered} is an interface that can be implemented by objects that
 * should be <em>orderable</em>, for example in a {@code Collection}.
 *
 * <p>
 * The actual {@link #getOrder() order} can be interpreted as prioritization,
 * with the first object (with the lowest order value) having the highest
 * priority.
 *
 * <p>
 * Note that there is also a <em>priority</em> marker for this interface:
 * {@link PriorityOrdered}. Order values expressed by {@code PriorityOrdered}
 * objects always apply before same order values expressed by <em>plain</em>
 * {@link Ordered} objects.
 *
 * <p>
 * Consult the Javadoc for {@link OrderComparator} for details on the sort
 * semantics for non-ordered objects.
 *
 * @see PriorityOrdered
 * @see OrderComparator
 */
public interface Ordered {

	/**
	 * Useful constant for the highest precedence value.
	 * 
	 * @see java.lang.Integer#MIN_VALUE
	 */
	int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

	int DEFAULT_PRECEDENCE = 0;

	/**
	 * Useful constant for the lowest precedence value.
	 * 
	 * @see java.lang.Integer#MAX_VALUE
	 */
	int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

	/**
	 * Get the order value of this object.
	 * <p>
	 * Higher values are interpreted as lower priority. As a consequence, the object
	 * with the lowest value has the highest priority (somewhat analogous to Servlet
	 * {@code load-on-startup} values).
	 * <p>
	 * Same order values will result in arbitrary sort positions for the affected
	 * objects.
	 * 
	 * @return the order value
	 * @see #HIGHEST_PRECEDENCE
	 * @see #LOWEST_PRECEDENCE
	 */
	int getOrder();

}
