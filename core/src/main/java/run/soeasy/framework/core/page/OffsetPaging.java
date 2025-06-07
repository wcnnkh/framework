package run.soeasy.framework.core.page;

import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Listable;

/**
 * 使用偏移量进行分页
 * 
 * @author soeasy.run
 *
 * @param <V>
 */
public class OffsetPaging<V> extends CursorPaging<Long, V> {
	/**
	 * 在内存中对list进行分页
	 * 
	 * @param offset
	 * @param pageSize
	 * @param elements
	 */
	public OffsetPaging(long offset, int pageSize, List<V> elements) {
		this(elements.size(), offset, pageSize, (cursorId, length) -> {
			int fromIndex = Math.toIntExact(cursorId);
			if (fromIndex >= elements.size()) {
				return Listable.empty();
			}
			List<V> list = elements.subList(fromIndex, Math.min(fromIndex + length, elements.size()));
			return Listable.forCollection(list);
		});
	}

	/**
	 * 未知数量的构造
	 * 
	 * @param offset
	 * @param pageSize
	 * @param pagingQuery
	 */
	public OffsetPaging(long offset, int pageSize, @NonNull PagingQuery<Long, V, Listable<V>> pagingQuery) {
		super(offset, pageSize, (cursorId, length) -> {
			Listable<V> listable = pagingQuery.query(cursorId, length);
			return new Cursor<>(cursorId, listable, listable.hasElements() ? (cursorId + length) : null);
		});
	}

	/**
	 * 已知总数的构造
	 * 
	 * @param total
	 * @param offset
	 * @param pageSize
	 * @param pagingQuery
	 */
	public OffsetPaging(long total, long offset, int pageSize, @NonNull PagingQuery<Long, V, Listable<V>> pagingQuery) {
		super(total, offset, pageSize, (cursorId, length) -> {
			long nextCursorId = cursorId + length;
			return new Cursor<>(cursorId, pagingQuery.query(cursorId, length),
					nextCursorId < total ? nextCursorId : null);
		});
	}

	public final long getPageNumber() {
		return (getCursorId() / getPageSize()) + 1;
	}

	public final OffsetPaging<V> jumpToPage(long pageNumber) {
		return jumpToPage(pageNumber, getPageSize());
	}

	public OffsetPaging<V> jumpToPage(long pageNumber, int pageSize) {
		return new OffsetPaging<>(getTotal(), pageNumber * pageSize, pageSize, this::query);
	}
}
