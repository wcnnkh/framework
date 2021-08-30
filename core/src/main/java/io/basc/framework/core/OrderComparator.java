package io.basc.framework.core;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.comparator.CompareUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link Comparator} implementation for {@link Ordered} objects, sorting by
 * order value ascending(根据数值进行升序), respectively by priority descending(根据优先级进行降序).
 *
 * <h3>Same Order Objects</h3>
 * <p>
 * Objects that have the same order value will be sorted with arbitrary ordering
 * with respect to other objects with the same order value.
 *
 * <h3>Non-ordered Objects</h3>
 * <p>
 * Any object that does not provide its own order value is implicitly assigned a
 * value of {@link Ordered#LOWEST_PRECEDENCE}, thus ending up at the end of a
 * sorted collection in arbitrary order with respect to other objects with the
 * same order value.
 *
 * @see Ordered
 * @see io.basc.framework.annotation.AnnotationAwareOrderComparator
 * @see java.util.Collections#sort(java.util.List, java.util.Comparator)
 * @see java.util.Arrays#sort(Object[], java.util.Comparator)
 */
public class OrderComparator implements Comparator<Object> {

	/**
	 * Shared default instance of {@code OrderComparator}.
	 */
	public static final OrderComparator INSTANCE = new OrderComparator();

	/**
	 * Build an adapted order comparator with the given source provider.
	 * 
	 * @param sourceProvider
	 *            the order source provider to use
	 * @return the adapted comparator
	 */
	public Comparator<Object> withSourceProvider(final OrderSourceProvider sourceProvider) {
		return new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return doCompare(o1, o2, sourceProvider);
			}
		};
	}

	public int compare(Object o1, Object o2) {
		return doCompare(o1, o2, null);
	}

	private int doCompare(Object o1, Object o2, OrderSourceProvider sourceProvider) {
		boolean p1 = (o1 instanceof PriorityOrdered);
		boolean p2 = (o2 instanceof PriorityOrdered);
		if (p1 && !p2) {
			return -1;
		} else if (p2 && !p1) {
			return 1;
		}

		// Direct evaluation instead of Integer.compareTo to avoid unnecessary
		// object creation.
		int i1 = getOrder(o1, sourceProvider);
		int i2 = getOrder(o2, sourceProvider);
		return compare(i1, i2);
	}
	
	public int compare(int o1, int o2){
		return CompareUtils.compare(o1, o2, false);
	}

	/**
	 * Determine the order value for the given object.
	 * <p>
	 * The default implementation checks against the given
	 * {@link OrderSourceProvider} using {@link #findOrder} and falls back to a
	 * regular {@link #getOrder(Object)} call.
	 * 
	 * @param obj
	 *            the object to check
	 * @return the order value, or {@code Ordered.LOWEST_PRECEDENCE} as fallback
	 */
	private int getOrder(Object obj, OrderSourceProvider sourceProvider) {
		Integer order = null;
		if (sourceProvider != null) {
			Object orderSource = sourceProvider.getOrderSource(obj);
			if (orderSource != null && orderSource.getClass().isArray()) {
				Object[] sources = ObjectUtils.toObjectArray(orderSource);
				for (Object source : sources) {
					order = findOrder(source);
					if (order != null) {
						break;
					}
				}
			} else {
				order = findOrder(orderSource);
			}
		}
		return (order != null ? order : getOrder(obj));
	}

	/**
	 * Determine the order value for the given object.
	 * <p>
	 * The default implementation checks against the {@link Ordered} interface
	 * through delegating to {@link #findOrder}. Can be overridden in
	 * subclasses.
	 * 
	 * @param obj
	 *            the object to check
	 * @return the order value, or {@code Ordered.LOWEST_PRECEDENCE} as fallback
	 */
	protected int getOrder(Object obj) {
		Integer order = findOrder(obj);
		return (order != null ? order : Ordered.DEFAULT_PRECEDENCE);
	}

	/**
	 * Find an order value indicated by the given object.
	 * <p>
	 * The default implementation checks against the {@link Ordered} interface.
	 * Can be overridden in subclasses.
	 * 
	 * @param obj
	 *            the object to check
	 * @return the order value, or {@code null} if none found
	 */
	protected Integer findOrder(Object obj) {
		return (obj instanceof Ordered ? ((Ordered) obj).getOrder() : null);
	}

	/**
	 * Determine a priority value for the given object, if any.
	 * <p>
	 * The default implementation always returns {@code null}. Subclasses may
	 * override this to give specific kinds of values a 'priority'
	 * characteristic, in addition to their 'order' semantics. A priority
	 * indicates that it may be used for selecting one object over another, in
	 * addition to serving for ordering purposes in a list/array.
	 * 
	 * @param obj
	 *            the object to check
	 * @return the priority value, or {@code null} if none
	 */
	public Integer getPriority(Object obj) {
		return null;
	}

	/**
	 * Sort the given List with a default OrderComparator.
	 * <p>
	 * Optimized to skip sorting for lists with size 0 or 1, in order to avoid
	 * unnecessary array extraction.
	 * 
	 * @param list
	 *            the List to sort
	 * @see java.util.Collections#sort(java.util.List, java.util.Comparator)
	 */
	public static void sort(List<?> list) {
		if (list.size() > 1) {
			Collections.sort(list, INSTANCE);
		}
	}

	/**
	 * Sort the given array with a default OrderComparator.
	 * <p>
	 * Optimized to skip sorting for lists with size 0 or 1, in order to avoid
	 * unnecessary array extraction.
	 * 
	 * @param array
	 *            the array to sort
	 * @see java.util.Arrays#sort(Object[], java.util.Comparator)
	 */
	public static void sort(Object[] array) {
		if (array.length > 1) {
			Arrays.sort(array, INSTANCE);
		}
	}

	/**
	 * Sort the given array or List with a default OrderComparator, if
	 * necessary. Simply skips sorting when given any other value.
	 * <p>
	 * Optimized to skip sorting for lists with size 0 or 1, in order to avoid
	 * unnecessary array extraction.
	 * 
	 * @param value
	 *            the array or List to sort
	 * @see java.util.Arrays#sort(Object[], java.util.Comparator)
	 */
	public static void sortIfNecessary(Object value) {
		if (value instanceof Object[]) {
			sort((Object[]) value);
		} else if (value instanceof List) {
			sort((List<?>) value);
		}
	}

	/**
	 * Strategy interface to provide an order source for a given object.
	 * 
	 */
	public interface OrderSourceProvider {

		/**
		 * Return an order source for the specified object, i.e. an object that
		 * should be checked for an order value as a replacement to the given
		 * object.
		 * <p>
		 * Can also be an array of order source objects.
		 * <p>
		 * If the returned object does not indicate any order, the comparator
		 * will fall back to checking the original object.
		 * 
		 * @param obj
		 *            the object to find an order source for
		 * @return the order source for that object, or {@code null} if none
		 *         found
		 */
		Object getOrderSource(Object obj);
	}

}
