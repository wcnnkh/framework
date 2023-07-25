package io.basc.framework.util.collect;

import java.util.LinkedHashMap;

/**
 * 线程不安全的
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 1L;
	private final int maxCapacity;

	public LRULinkedHashMap(int maxCapacity) {
		this(maxCapacity, maxCapacity + 1, 1);
	}

	public LRULinkedHashMap(int maxCapacity, int initialCapacity, float loadFactor) {
		this(maxCapacity, initialCapacity, loadFactor, false);
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
