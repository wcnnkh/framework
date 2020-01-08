package scw.util.comparator;

import java.util.Comparator;

import scw.core.Assert;

public class InstanceComparator<T> implements Comparator<T> {
	private final Class<?>[] instanceOrder;

	/**
	 * Create a new {@link InstanceComparator} instance.
	 * 
	 * @param instanceOrder
	 *            the ordered list of classes that should be used when comparing
	 *            objects. Classes earlier in the list will be given a higher
	 *            priority.
	 */
	public InstanceComparator(Class<?>... instanceOrder) {
		Assert.notNull(instanceOrder, "'instanceOrder' must not be null");
		this.instanceOrder = instanceOrder;
	}

	public int compare(T o1, T o2) {
		int i1 = getOrder(o1);
		int i2 = getOrder(o2);
		return (i1 < i2 ? -1 : (i1 == i2 ? 0 : 1));
	}

	private int getOrder(T object) {
		if (object != null) {
			for (int i = 0; i < this.instanceOrder.length; i++) {
				if (this.instanceOrder[i].isInstance(object)) {
					return i;
				}
			}
		}
		return this.instanceOrder.length;
	}

}