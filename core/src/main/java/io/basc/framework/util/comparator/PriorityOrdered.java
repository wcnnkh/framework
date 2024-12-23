package io.basc.framework.util.comparator;

/**
 * Extension of the {@link Ordered} interface, expressing a <em>priority</em>
 * ordering: {@code PriorityOrdered} objects are always applied before
 * <em>plain</em> {@link Ordered} objects regardless of their order values.
 *
 * <p>
 * When sorting a set of {@code Ordered} objects, {@code PriorityOrdered}
 * objects and <em>plain</em> {@code Ordered} objects are effectively treated as
 * two separate subsets, with the set of {@code PriorityOrdered} objects
 * preceding the set of <em>plain</em> {@code Ordered} objects and with relative
 * ordering applied within those subsets.
 *
 * <p>
 * Note: {@code PriorityOrdered} post-processor beans are initialized in a
 * special phase, ahead of other post-processor beans. This subtly affects their
 * autowiring behavior: they will only be autowired against beans which do not
 * require eager initialization for type matching.
 *
 */
public interface PriorityOrdered extends Ordered {
}
