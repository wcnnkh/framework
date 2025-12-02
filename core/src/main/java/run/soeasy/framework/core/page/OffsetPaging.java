package run.soeasy.framework.core.page;

import java.util.Collection;
import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Listable;

/**
 * 使用偏移量进行分页 基于偏移量(offset)和每页大小(pageSize)实现的分页机制
 * 
 * @author soeasy.run
 *
 * @param <V> 分页内容的元素类型
 */
public class OffsetPaging<V> extends CursorPaging<Long, V> {
	/**
	 * 在内存中对list进行分页
	 * 
	 * @param offset   偏移量，从0开始
	 * @param pageSize 每页大小
	 * @param elements 原始数据列表
	 */
	public OffsetPaging(long offset, int pageSize, List<V> elements) {
		this(elements.size(), offset, pageSize, (cursorId, length) -> {
			int fromIndex = Math.toIntExact(cursorId);
			if (fromIndex >= elements.size()) {
				return Listable.empty();
			}
			List<V> list = elements.subList(fromIndex, Math.min(Math.addExact(fromIndex, length), elements.size()));
			return Listable.forCollection(list);
		});
	}

	/**
	 * 未知数量的构造
	 * 
	 * @param offset      偏移量，从0开始
	 * @param pageSize    每页大小
	 * @param pagingQuery 分页查询器，不可为null
	 */
	public OffsetPaging(long offset, int pageSize, @NonNull PagingQuery<Long, Listable<V>> pagingQuery) {
		super(offset, pageSize, (cursorId, length) -> {
			Listable<V> listable = pagingQuery.query(cursorId, length);
			long nextCursorId = Math.addExact(cursorId, length);// 应该给异常还是返回没有下一页？
			return new Cursor<>(cursorId, listable, listable.hasElements() ? nextCursorId : null);
		});
	}

	/**
	 * 已知总数的构造
	 * 
	 * @param total       总记录数
	 * @param offset      偏移量，从0开始
	 * @param pageSize    每页大小
	 * @param pagingQuery 分页查询器，不可为null
	 */
	public OffsetPaging(long total, long offset, int pageSize, @NonNull PagingQuery<Long, Listable<V>> pagingQuery) {
		super(total, offset, pageSize, (cursorId, length) -> {
			long nextCursorId = Math.addExact(cursorId, length);
			return new Cursor<>(cursorId, pagingQuery.query(cursorId, length),
					nextCursorId < total ? nextCursorId : null);
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

	/**
	 * 基于偏移量创建OffsetPaging实例（未知总条数）
	 * 
	 * @param <E>         分页元素类型
	 * @param offset      偏移量，从0开始
	 * @param pageSize    每页大小，需大于0
	 * @param offsetQuery 偏移量查询器，入参为偏移量和每页大小，返回对应数据集
	 * @return 未知总条数的OffsetPaging实例
	 */
	public static <E> OffsetPaging<E> of(long offset, int pageSize,
			PagingQuery<Long, ? extends Collection<E>> offsetQuery) {
		return new OffsetPaging<>(offset, pageSize, (o, limit) -> {
			Collection<E> elements = offsetQuery.query(o, limit);
			if (CollectionUtils.isEmpty(elements)) {
				return Listable.empty();
			}
			return Listable.forCollection(elements);
		});
	}

	/**
	 * 基于页码创建OffsetPaging实例（未知总条数）
	 * 
	 * @param <E>         分页元素类型
	 * @param pageNumber  页码，从1开始
	 * @param pageSize    每页大小，需大于0
	 * @param pageQuery   页码查询器，入参为页码和每页大小，返回对应数据集
	 * @return 未知总条数的OffsetPaging实例
	 */
	public static <E> OffsetPaging<E> ofUnknownPageNumber(long pageNumber, int pageSize,
			PagingQuery<Long, ? extends Collection<E>> pageQuery) {
		return of(getOffset(pageNumber, pageSize), pageSize, (offset, limit) -> {
			return pageQuery.query(getPageNumber(offset, limit), pageSize);
		});
	}

	/**
	 * 基于偏移量和总条数创建OffsetPaging实例（已知总条数）
	 * 
	 * @param <E>         分页元素类型
	 * @param total       总记录数，需大于等于0
	 * @param offset      偏移量，从0开始
	 * @param pageSize    每页大小，需大于0
	 * @param offsetQuery 偏移量查询器，入参为偏移量和每页大小，返回对应数据集
	 * @return 已知总条数的OffsetPaging实例
	 */
	public static <E> OffsetPaging<E> of(long total, long offset, int pageSize,
			PagingQuery<Long, ? extends Collection<E>> offsetQuery) {
		return new OffsetPaging<>(total, offset, pageSize, (o, limit) -> {
			Collection<E> elements = offsetQuery.query(o, limit);
			if (CollectionUtils.isEmpty(elements)) {
				return Listable.empty();
			}
			return Listable.forCollection(elements);
		});
	}

	/**
	 * 基于页码和总条数创建OffsetPaging实例（已知总条数）
	 * 
	 * @param <E>         分页元素类型
	 * @param total       总记录数，需大于等于0
	 * @param pageNumber  页码，从1开始
	 * @param pageSize    每页大小，需大于0
	 * @param pageQuery   页码查询器，入参为页码和每页大小，返回对应数据集
	 * @return 已知总条数的OffsetPaging实例
	 */
	public static <E> OffsetPaging<E> ofPageNumber(long total, long pageNumber, int pageSize,
			PagingQuery<Long, ? extends Collection<E>> pageQuery) {
		return of(total, getOffset(pageNumber, pageSize), pageSize, (offset, limit) -> {
			return pageQuery.query(getPageNumber(offset, limit), pageSize);
		});
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
		return new OffsetPaging<>(getTotal(), getOffset(pageNumber, pageSize), pageSize, this::query);
	}
}