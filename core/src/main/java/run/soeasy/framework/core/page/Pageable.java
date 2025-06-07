package run.soeasy.framework.core.page;

import run.soeasy.framework.core.collection.Listable;

/**
 * 可分页的对象
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 */
public interface Pageable<K, V> extends Listable<V> {
	/**
	 * 当前游标id
	 * 
	 * @return
	 */
	K getCursorId();

	/**
	 * 下一个游标id
	 * 
	 * @return
	 */
	K getNextCursorId();

	/**
	 * 是否有下一页
	 * 
	 * @return
	 */
	default boolean hasNextPage() {
		return getNextCursorId() != null;
	}
}
