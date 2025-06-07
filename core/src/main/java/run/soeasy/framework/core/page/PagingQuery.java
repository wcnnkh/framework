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
	 * @param pageSize pageSize是一个int类型的原因是如果每页的数量太大那分页就没有意义.（另外不使用sort类型的原因是java中short类型是用int实现的）
	 * @return
	 */
	T query(S cursorId, int pageSize);
}
