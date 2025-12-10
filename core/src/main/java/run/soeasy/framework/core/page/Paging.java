package run.soeasy.framework.core.page;

import java.util.NoSuchElementException;

import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.LinkedIterator;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 基于游标的分页接口 定义基于游标的分页查询机制，支持获取分页元数据、页跳转及全量页的延迟遍历
 * 
 * @author soeasy.run
 * @param <K> 游标类型，用于标识分页起始位置（如Long/String/复合标识）
 * @param <V> 分页内容的元素类型（如业务实体类）
 */
public interface Paging<K, V> extends Slice<K, V>, PagingQuery<K, Paging<K, V>> {
	/**
	 * 计算总页数（适配大数据量，避免数值溢出）
	 * <p>
	 * 公式说明：(totalCount - 1) / pageSize + 1 <br>
	 * 优势：替代 (totalCount + pageSize - 1)/pageSize，避免大数相加导致的溢出，且结果等价（totalCount>0时）
	 * </p>
	 * 
	 * @param totalCount 总记录数，必须 ≥ 0
	 * @param pageSize   每页大小，必须 > 0
	 * @return 总页数（≥ 0）
	 * @throws IllegalArgumentException 当totalCount < 0 或 pageSize ≤ 0时抛出
	 * @throws ArithmeticException      当计算过程中（如加法）发生数值溢出时抛出
	 */
	public static long getTotalPages(long totalCount, long pageSize) {
		Assert.isTrue(totalCount >= 0, "Total count must be greater than or equal to 0");
		Assert.isTrue(pageSize > 0, "PageSize must be greater than 0");

		if (totalCount == 0) {
			return 0;
		}

		// 等价变形公式：(totalCount - 1) / pageSize + 1（避免totalCount + pageSize溢出）
		return Math.addExact(((totalCount - 1) / pageSize), 1);
	}

	/**
	 * 获取总页数（基于当前分页的总记录数和页大小计算）
	 * 
	 * @return 总页数
	 * @throws IllegalArgumentException 当总记录数<0 或 页大小≤0时抛出
	 * @throws ArithmeticException      当计算过程中发生数值溢出时抛出
	 */
	default long getTotalPages() {
		Long total = getTotalCount();
		if (total == null) {
			throw new UnsupportedOperationException("Unknown total number");
		}
		return getTotalPages(total, getPageSize());
	}

	/**
	 * 获取每页数量
	 * 
	 * @return 每页数量
	 */
	int getPageSize();

	/**
	 * 获取下一页的分页对象
	 * 
	 * @return 下一页的分页对象
	 * @throws NoSuchElementException 如果没有下一页数据时抛出
	 */
	default Paging<K, V> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next page");
		}
		return jumpTo(getNextCursor());
	}

	/**
	 * 获取所有页的元素集合（延迟加载） 支持流式遍历所有页，后续页数据仅在遍历到时分页查询，避免一次性加载全量数据（适配大数据集）
	 * 
	 * @return 包含所有页的延迟加载元素集合
	 */
	default Streamable<Paging<K, V>> pages() {
		return Streamable.of(() -> new LinkedIterator<>(this, Paging::hasNext, Paging::next));
	}

	/**
	 * 跳转到指定游标位置的分页
	 * 
	 * @param cursor 目标页的游标（首次查询可为null）
	 * @return 指定游标位置的分页对象
	 */
	default Paging<K, V> jumpTo(K cursor) {
		return query(cursor, getPageSize());
	}
}