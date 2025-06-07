package run.soeasy.framework.core.page;

import run.soeasy.framework.core.collection.Listable;

/**
 * 分页查询
 * 
 * @author soeasy.run
 *
 * @param <S>
 * @param <E>
 * @param <T>
 */
public interface PagingQuery<S, E, T extends Listable<E>> {
	/**
	 * 根据指定游标进行查询
	 * 
	 * @param cursorId
	 * @param pageSize 0表示未知大小
	 * @return
	 */
	T query(S cursorId, int pageSize);
}
