package run.soeasy.framework.core.page;

import run.soeasy.framework.core.collection.Listable;

/**
 * 可分页的对象 定义基于游标的分页机制，用于高效处理大数据集的分页查询
 * 
 * @author soeasy.run
 *
 * @param <K> 游标的类型，用于标识分页位置
 * @param <V> 分页内容的元素类型
 */
public interface Pageable<K, V> extends Listable<V> {
	/**
	 * 获取当前页的游标ID 游标ID用于标识当前分页位置，通常是上一页最后一个元素的唯一标识
	 * 
	 * @return 当前游标ID，首次请求时可为null
	 */
	K getCursorId();

	/**
	 * 获取下一页的游标ID 用于标识下一页数据的起始位置
	 * 
	 * @return 下一页游标ID，若无下一页则返回null
	 */
	K getNextCursorId();

	/**
	 * 判断是否存在下一页 基于{@link #getNextCursorId()}的返回值进行判断
	 * 
	 * @return 存在下一页返回true，否则返回false
	 */
	default boolean hasNextPage() {
		return getNextCursorId() != null;
	}

	/**
	 * 判断是否已知总数 某些场景下（如大数据集）可能无法高效获取总数，此时返回false 未知总数的分页在调用getTotal()时会使用代价较高的循环计数
	 * 
	 * @return 如果已知总数返回true，否则返回false
	 */
	default boolean isKnowTotal() {
		return getTotal() != null;
	}

	/**
	 * 获取总记录数 如果isKnowTotal()返回false，此方法可能会触发全量数据遍历，性能开销较大
	 * 
	 * @return 总记录数
	 */
	Long getTotal();
}