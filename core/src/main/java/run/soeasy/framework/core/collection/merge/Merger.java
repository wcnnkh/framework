package run.soeasy.framework.core.collection.merge;

import run.soeasy.framework.core.collection.select.Selector;

/**
 * 合并器
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
@FunctionalInterface
public interface Merger<E> extends Selector<E> {

	/**
	 * 展开
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Merger<T> flat() {
		return (Merger<T>) FlatMerger.INSTANCE;
	}

	/**
	 * 将多个map合并
	 * 
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> MapMerger<K, V> map() {
		return (MapMerger<K, V>) MapMerger.INSTANCE;
	}

	/**
	 * 将多个properties合并
	 * 
	 * @return
	 */
	public static PropertiesMerger properties() {
		return PropertiesMerger.INSTANCE;
	}
}
