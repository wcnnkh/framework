package run.soeasy.framework.core.page;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.Listable;

/**
 * 使用偏移量进行分页 基于偏移量(offset)和每页大小(pageSize)实现的分页机制
 * 
 * @author soeasy.run
 *
 * @param <V> 分页内容的元素类型
 */
public class OffsetPaging<V> extends CursorPaging<Long, V> {

	public <T> OffsetPaging(long offset, int pageSize, @NonNull PagingQuery<Long, ? extends T> offsetQuery,
			@NonNull Function<? super T, ? extends Listable<V>> elementMapper,
			@NonNull Function<? super T, ? extends Number> totalMapper) {
		super(offset, pageSize, (cursorId, length) -> {
			T result = offsetQuery.query(cursorId, length);
			Listable<V> elements = result == null ? null : elementMapper.apply(result);
			if (elements == null) {
				elements = Listable.empty();
			}
			Number totalNumber = result == null ? null : totalMapper.apply(result);
			Long total = totalNumber == null ? null : totalNumber.longValue();
			Long nextCursorId;
			if (total == null) {
				// 未知数量
				nextCursorId = elements.hasElements() ? Math.addExact(cursorId, length) : null;
			} else {
				// 已知数量
				nextCursorId = (total - cursorId) > 1 ? Math.addExact(cursorId, length) : null;
			}
			return new Cursor<>(cursorId, elements, nextCursorId, total);
		});
	}

	/**
	 * 获取页码
	 * 
	 * @param offset   偏移量, 从0开始
	 * @param pageSize 每页大小, 不能小于0
	 * @return 从1开始
	 */
	public static long getPageNumber(long offset, long pageSize) {
		Assert.isTrue(offset >= 0, "Offset must be greater than or equal to 0");
		Assert.isTrue(pageSize > 0, "PageSize must be greater than to 0");
		return Math.addExact(offset / pageSize, 1);
	}

	/**
	 * 获取偏移量
	 * 
	 * @param pageNumber 页码，从1开始
	 * @param pageSize   每页大小, 不能小于0
	 * @return
	 */
	public static long getOffset(long pageNumber, long pageSize) {
		Assert.isTrue(pageNumber > 0, "PageNumber must be greater than to 0");
		Assert.isTrue(pageSize > 0, "PageSize must be greater than to 0");
		return Math.multiplyExact((pageNumber - 1), pageSize);
	}

	public static <E> OffsetPaging<E> of(long offset, int pageSize, List<E> elements) {
		return of(offset, pageSize, (cursorId, length) -> {
			int fromIndex = Math.toIntExact(cursorId);
			if (fromIndex >= elements.size()) {
				return Collections.emptyList();
			}
			return elements.subList(fromIndex, Math.min(Math.addExact(fromIndex, length), elements.size()));
		}, Function.identity(), (e) -> elements.size());
	}

	public static <E> OffsetPaging<E> ofPageNumber(long pageNumber, int pageSize, List<E> elements) {
		return of(getPageNumber(pageNumber, pageSize), pageSize, elements);
	}

	public static <T, E> OffsetPaging<E> of(long offset, int pageSize,
			@NonNull PagingQuery<Long, ? extends T> offsetQuery,
			@NonNull Function<? super T, ? extends Collection<E>> elementMapper,
			@NonNull Function<? super T, ? extends Number> totalMapper) {
		return new OffsetPaging<>(offset, pageSize, offsetQuery, (e) -> {
			Collection<E> elements = elementMapper.apply(e);
			return elements == null ? null : Listable.forCollection(elements);
		}, totalMapper);
	}

	public static <T, E> OffsetPaging<E> ofPageNumber(long pageNumber, int pageSize,
			@NonNull PagingQuery<Long, ? extends T> pageNumberQuery,
			@NonNull Function<? super T, ? extends Collection<E>> elementMapper,
			@NonNull Function<? super T, ? extends Number> totalMapper) {
		return of(getOffset(pageNumber, pageSize), pageSize, (offset, limit) -> {
			return pageNumberQuery.query(getPageNumber(offset, limit), limit);
		}, elementMapper, totalMapper);
	}

	public static <T, E> OffsetPaging<E> of(long offset, int pageSize,
			@NonNull PagingQuery<Long, ? extends Collection<E>> offsetQuery, Long total) {
		return of(offset, pageSize, offsetQuery, Function.identity(), (e) -> total);
	}

	public static <T, E> OffsetPaging<E> ofPageNumber(long pageNumber, int pageSize,
			@NonNull PagingQuery<Long, ? extends Collection<E>> offsetQuery, Long total) {
		return ofPageNumber(pageNumber, pageSize, offsetQuery, Function.identity(), (e) -> total);
	}

	/**
	 * 获取当前页码
	 * 
	 * @return 当前页码，从1开始
	 */
	public final long getPageNumber() {
		return getPageNumber(getCursorId(), getPageSize());
	}

	/**
	 * 跳转到指定页码
	 * 
	 * @param pageNumber 目标页码，从1开始
	 * @return 新的OffsetPaging实例
	 */
	public final OffsetPaging<V> jumpToPage(long pageNumber) {
		return jumpToPage(pageNumber, getPageSize());
	}

	/**
	 * 跳转到指定页码并指定每页大小
	 * 
	 * @param pageNumber 目标页码，从1开始
	 * @param pageSize   每页大小
	 * @return 新的OffsetPaging实例
	 */
	public OffsetPaging<V> jumpToPage(long pageNumber, int pageSize) {
		return new OffsetPaging<>(getOffset(pageNumber, pageSize), pageSize, this::query, Function.identity(),
				(e) -> e.isKnowTotal() ? e.getTotal() : null);
	}
}