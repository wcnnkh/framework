package run.soeasy.framework.core.page;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Listable;

/**
 * 基于游标的分页
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 */
@RequiredArgsConstructor
public class CursorPaging<K, V> implements Paging<K, V> {
	private volatile Pageable<K, V> currentPage;
	private final Long total;
	private final K cursorId;
	private final int pageSize;
	@NonNull
	private final PagingQuery<K, Pageable<K, V>> pagingQuery;

	/**
	 * 每页无数量限制的分页
	 * 
	 * @param cursorId
	 * @param pagingQuery
	 */
	public CursorPaging(K cursorId, @NonNull PagingQuery<K, Pageable<K, V>> pagingQuery) {
		this(cursorId, 0, pagingQuery);
	}

	public CursorPaging(K cursorId, int pageSize, @NonNull PagingQuery<K, Pageable<K, V>> pagingQuery) {
		this(null, cursorId, pageSize, pagingQuery);
	}

	public final Pageable<K, V> getCurrentPage() {
		if (currentPage == null) {
			synchronized (this) {
				if (currentPage == null) {
					currentPage = pagingQuery.query(cursorId, pageSize);
					if (currentPage == null) {
						// 没数据了
						currentPage = new Cursor<>(cursorId, Listable.empty(), null);
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
		return total != null;
	}

	@Override
	public long getTotal() {
		return total == null ? pages().stream().mapToLong((e) -> e.getElements().count()).sum() : total;
	}

	@Override
	public final boolean hasNextPage() {
		return getCurrentPage().hasNextPage();
	}

	@Override
	public Paging<K, V> query(K cursorId, int pageSize) {
		return new CursorPaging<>(total, cursorId, pageSize, pagingQuery);
	}

}
