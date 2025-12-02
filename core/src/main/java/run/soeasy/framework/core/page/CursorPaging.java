package run.soeasy.framework.core.page;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Listable;

/**
 * 基于游标的分页实现 使用懒加载机制，首次访问数据时执行实际查询
 * 
 * @author soeasy.run
 *
 * @param <K> 游标的类型，用于标识分页位置
 * @param <V> 分页内容的元素类型
 */
@RequiredArgsConstructor
public class CursorPaging<K, V> implements Paging<K, V> {
	/** 当前页数据，使用双重检查锁定机制延迟初始化 */
	private volatile Pageable<K, V> currentPage;
	/** 总记录数，null表示总数未知，需遍历所有页计算 */
	private final Long total;
	/** 当前页的游标ID */
	private final K cursorId;
	/** 每页大小，0表示使用默认值 */
	private final int pageSize;
	/** 分页查询器，不可为null */
	@NonNull
	private final PagingQuery<K, Pageable<K, V>> pagingQuery;

	/**
	 * 创建每页无固定数量限制的分页
	 * 
	 * @param cursorId    起始游标ID
	 * @param pagingQuery 分页查询器，不可为null
	 */
	public CursorPaging(K cursorId, @NonNull PagingQuery<K, Pageable<K, V>> pagingQuery) {
		this(cursorId, 0, pagingQuery);
	}

	/**
	 * 创建指定每页大小的分页
	 * 
	 * @param cursorId    起始游标ID
	 * @param pageSize    每页大小，0表示使用默认值
	 * @param pagingQuery 分页查询器，不可为null
	 */
	public CursorPaging(K cursorId, int pageSize, @NonNull PagingQuery<K, Pageable<K, V>> pagingQuery) {
		this(null, cursorId, pageSize, pagingQuery);
	}

	/**
	 * 获取当前页数据，使用双重检查锁定实现懒加载 首次调用时执行实际查询，并缓存结果
	 * 
	 * @return 当前页的Pageable对象
	 */
	private Pageable<K, V> getCurrentPage() {
		if (currentPage == null) {
			synchronized (this) {
				if (currentPage == null) {
					currentPage = pagingQuery.query(cursorId, pageSize);
					if (currentPage == null) {
						// 处理无数据情况
						currentPage = new Cursor<>(cursorId, Listable.empty(), null, null);
					}
				}
			}
		}
		return currentPage;
	}

	@Override
	public final K getCursorId() {
		return cursorId;
	}

	@Override
	public final Elements<V> getElements() {
		return getCurrentPage().getElements();
	}

	@Override
	public final K getNextCursorId() {
		return getCurrentPage().getNextCursorId();
	}

	@Override
	public final int getPageSize() {
		return pageSize;
	}

	@Override
	public boolean isKnowTotal() {
		return total != null || (currentPage != null && currentPage.isKnowTotal());
	}

	@Override
	public Long getTotal() {
		if (total != null) {
			return total;
		}

		if (getCurrentPage().isKnowTotal()) {
			return getCurrentPage().getTotal();
		}

		long total = 0;
		for (Paging<K, V> paging : pages()) {
			if (paging.isKnowTotal()) {
				return paging.getTotal();
			}
			total = Math.addExact(paging.getElements().count(), total);
		}
		return total;
	}

	@Override
	public final boolean hasNextPage() {
		return getCurrentPage().hasNextPage();
	}

	@Override
	public Paging<K, V> query(K cursorId, int pageSize) {
		return new CursorPaging<>(isKnowTotal() ? getTotal() : total, cursorId, pageSize, pagingQuery);
	}
}