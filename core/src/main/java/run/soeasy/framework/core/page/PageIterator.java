package run.soeasy.framework.core.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 基于元素迭代器的分页迭代器
 * <p>
 * 将单元素迭代器（Iterator<E>）拆分为按页划分的Pageable<Long, E>迭代器，
 * 每次迭代返回包含指定数量元素的分页对象，分页位置通过偏移量（offset）标识
 * </p>
 *
 * @param <E> 分页元素的类型
 * @author soeasy.run
 */
public class PageIterator<E> implements Iterator<Pageable<Long, E>> {
	/**
	 * 每页大小，需大于0
	 */
	@Getter
	private final int pageSize;

	/**
	 * 原始元素迭代器，提供分页的基础数据来源，不可为null
	 */
	@NonNull
	private final Iterator<? extends E> iterator;

	/**
	 * 下一页的页码（从1开始计数），用于调用OffsetPaging.getOffset计算偏移量
	 */
	private int pageNumber = 1;

	/**
	 * 缓存下一页的分页对象，减少重复计算
	 */
	private Pageable<Long, E> nextPageable;

	/**
	 * 创建分页迭代器
	 *
	 * @param pageSize 每页大小，必须大于0
	 * @param iterator 原始元素迭代器，不可为null
	 * @throws IllegalArgumentException 若pageSize≤0时抛出
	 * @throws NullPointerException     若iterator为null时抛出
	 */
	public PageIterator(int pageSize, @NonNull Iterator<? extends E> iterator) {
		Assert.isTrue(pageSize > 0, "PageSize must be greater than 0");
		this.pageSize = pageSize;
		this.iterator = iterator;
	}

	/**
	 * 判断是否存在下一页数据
	 * <p>
	 * 首次调用时会预加载下一页的元素并缓存分页对象，后续调用直接返回缓存状态
	 * </p>
	 *
	 * @return true表示存在下一页，false表示无更多分页数据
	 */
	@Override
	public synchronized boolean hasNext() {
		// 已缓存下一页，直接返回存在
		if (nextPageable != null) {
			return true;
		}
		// 原始迭代器无元素，无更多分页
		if (!iterator.hasNext()) {
			return false;
		}

		// 收集当前页的元素（最多pageSize个）
		List<E> currentPageElements = new ArrayList<>(pageSize);
		while (iterator.hasNext() && currentPageElements.size() < pageSize) {
			currentPageElements.add(iterator.next());
		}

		// 计算当前页偏移量（复用框架内置工具方法，保证逻辑统一）
		long offset = OffsetPaging.getOffset(pageNumber, pageSize);
		// 页码自增，为下一页偏移量计算做准备
		pageNumber++;

		// 计算下一页游标ID，防护数值溢出并补充上下文
		Long nextCursorId = null;
		try {
			nextCursorId = Math.addExact(offset, pageSize);
		} catch (ArithmeticException e) {
			throw new ArithmeticException(
					"Calculate next cursor id failed: overflow (offset=" + offset + ", pageSize=" + pageSize + ")");
		}

		// 构建分页对象并缓存
		nextPageable = new CursorPage<>(offset, Streamable.of(currentPageElements), nextCursorId, null);
		return true;
	}

	/**
	 * 获取下一页的分页对象
	 *
	 * @return 下一页的Pageable<Long, E>实例
	 * @throws NoSuchElementException 若不存在下一页时抛出
	 */
	@Override
	public synchronized Pageable<Long, E> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more pages available");
		}
		// 取出缓存的分页对象并清空缓存
		Pageable<Long, E> currentPage = nextPageable;
		nextPageable = null;
		return currentPage;
	}
}