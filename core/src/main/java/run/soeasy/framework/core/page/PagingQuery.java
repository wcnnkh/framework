package run.soeasy.framework.core.page;

/**
 * 分页查询
 * 
 * @author soeasy.run
 *
 * @param <S>
 * @param <T>
 */
public interface PagingQuery<S, T> {
	/**
	 * 根据指定游标进行查询
	 * 
	 * @param cursorId
	 * @param pageSize 0表示未知大小
	 * @return
	 */
	T query(S cursorId, int pageSize);
}
