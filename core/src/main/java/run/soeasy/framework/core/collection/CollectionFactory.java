package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Collections;

import run.soeasy.framework.core.domain.ObjectOperator;

@FunctionalInterface
public interface CollectionFactory<E, T extends Collection<E>> extends ObjectOperator<Collection<E>> {
	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	@Override
	default T create() {
		return createCollection(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	@Override
	default Collection<E> display(Collection<E> source) {
		return Collections.unmodifiableCollection(source);
	}

	@Override
	default T clone(Collection<E> source) {
		T target = createCollection(source.size(), DEFAULT_LOAD_FACTOR);
		target.addAll(source);
		return target;
	}

	/**
	 * 使用数组实现的都会有一个初始容量(initialCapacity)和扩容因子(loadFactor)
	 * 
	 * @param initialCapacity
	 * @param loadFactor
	 * @return
	 */
	T createCollection(int initialCapacity, float loadFactor);
}
