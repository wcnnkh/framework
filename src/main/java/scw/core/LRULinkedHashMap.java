package scw.core;

import java.util.LinkedHashMap;

/**
 * 线程不安全的
 * @author shuchaowen
 *
 * @param <K>
 * @param <V>
 */
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> implements LRU<K, V> {
	private static final long serialVersionUID = 1L;
	private final int maxCapacity;

	public LRULinkedHashMap(int maxCapacity, boolean accessOrder) {
		this(maxCapacity, maxCapacity + 1, 1, accessOrder);
	}

	public LRULinkedHashMap(int maxCapacity, int initialCapacity, float loadFactor) {
		this(maxCapacity, initialCapacity, loadFactor, false);
	}

	public LRULinkedHashMap(int maxCapacity, int initialCapacity, boolean accessOrder) {
		super(initialCapacity, 0.75f, accessOrder);
		this.maxCapacity = maxCapacity;
	}

	public LRULinkedHashMap(int maxCapacity, int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
		this.maxCapacity = maxCapacity;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	@Override
	protected final boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > getMaxCapacity();
	}
}
