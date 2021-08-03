package scw.util.page;

public interface Pageable<T> extends Cursor<Long, T> {
	/**
	 * 总页数
	 * 
	 * @return
	 */
	Long getPages();

	/**
	 * 总数
	 * 
	 * @return
	 */
	Long getTotal();
}
