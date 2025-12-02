package run.soeasy.framework.core.page;

import java.util.NoSuchElementException;

import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.LinkedIterator;

/**
 * 分页接口 定义基于游标的分页查询机制，支持获取分页元数据和遍历所有页
 * 
 * @author soeasy.run
 *
 * @param <K> 游标的类型，用于标识分页位置
 * @param <V> 分页内容的元素类型
 */
public interface Paging<K, V> extends Pageable<K, V>, PagingQuery<K, Paging<K, V>> {
	/**
	 * 获取总页数
	 * 
	 * @param total    总记录数，需 ≥ 0
	 * @param pageSize 每页大小，需 > 0
	 * @return 总页数（≥ 0）
	 * @throws ArithmeticException 当计算过程中发生数值溢出时抛出
	 */
	public static long getPages(long total, long pageSize) {
		Assert.isTrue(total >= 0, "Total must be greater than or equal to 0");
		Assert.isTrue(pageSize > 0, "PageSize must be greater than 0");

		if (total == 0) {
			return 0;
		}

		// 3. 等价变形公式：(total - 1) / pageSize + 1（避免total + pageSize溢出）
		// 原公式 (total + pageSize -1)/pageSize 等价于 (total-1)/pageSize + 1（total>0时）
		// 优势：仅做减法/除法/加法，无大数相加，从根源避免溢出
		return Math.addExact(((total - 1) / pageSize), 1);
	}

	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 * @throws ArithmeticException 如果pageSize为0
	 */
	default long getPages() {
		return getPages(getTotal(), getPageSize());
	}

	/**
	 * 获取每页数量
	 * 
	 * @return 每页数量，如果返回0表示每页数量不确定
	 */
	int getPageSize();

	/**
	 * 获取下一页
	 * 
	 * @return 下一页的分页对象
	 * @throws NoSuchElementException 如果没有下一页
	 */
	default Paging<K, V> nextPage() {
		if (!hasNextPage()) {
			throw new NoSuchElementException("There is no next page");
		}
		return jumpTo(getNextCursorId());
	}

	/**
	 * 获取所有页的元素集合 支持流式遍历所有页，延迟加载后续页的数据
	 * 
	 * @return 包含所有页的元素集合
	 */
	default Elements<Paging<K, V>> pages() {
		return Elements.of(() -> new LinkedIterator<>(this, Paging::hasNextPage, Paging::nextPage));
	}

	/**
	 * 跳转到指定游标位置的页
	 * 
	 * @param cursorId 目标页的游标ID
	 * @return 指定位置的分页对象
	 */
	default Paging<K, V> jumpTo(K cursorId) {
		return query(cursorId, getPageSize());
	}
}