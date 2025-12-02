package run.soeasy.framework.core.page;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.NumberUtils;
import run.soeasy.framework.core.collection.Listable;

/**
 * 基于偏移量的分页实现 基于偏移量(offset)和每页大小(pageSize)实现的分页机制，继承游标分页体系（偏移量作为Long型游标）
 * 
 * @author soeasy.run
 * @param <V> 分页内容的元素类型
 */
public class OffsetPaging<V> extends CursorPaging<Long, V> {

	/**
	 * 计算偏移量（基于页码和每页大小）
	 * 
	 * @param pageNumber 页码，必须≥1（从1开始）
	 * @param pageSize   每页大小，必须>0（不允许为0或负数）
	 * @return 偏移量（从0开始）
	 * @throws IllegalArgumentException 页码≤0或每页大小≤0时抛出
	 * @throws ArithmeticException      数值计算溢出时抛出
	 */
	public static long getOffset(long pageNumber, long pageSize) {
		Assert.isTrue(pageNumber > 0, "Page number must be greater than 0, cannot be 0 or negative");
		Assert.isTrue(pageSize > 0, "Page size must be greater than 0, cannot be 0 or negative");
		return Math.multiplyExact((pageNumber - 1), pageSize);
	}

	/**
	 * 计算页码（基于偏移量和每页大小）
	 * 
	 * @param offset   偏移量，必须≥0（从0开始）
	 * @param pageSize 每页大小，必须>0（不允许为0或负数）
	 * @return 页码（从1开始）
	 * @throws IllegalArgumentException 偏移量<0或每页大小≤0时抛出
	 * @throws ArithmeticException      数值计算溢出时抛出
	 */
	public static long getPageNumber(long offset, long pageSize) {
		Assert.isTrue(offset >= 0, "Offset must be greater than or equal to 0");
		Assert.isTrue(pageSize > 0, "Page size must be greater than 0, cannot be 0 or negative");
		return Math.addExact(offset / pageSize, 1);
	}

	/**
	 * 基于偏移量和内存列表创建OffsetPaging实例（内存分页）
	 * 
	 * @param <E>      分页元素类型
	 * @param offset   偏移量，必须≥0（从0开始）
	 * @param pageSize 每页大小，必须>0（不允许为0或负数）
	 * @param elements 原始数据列表，基于该列表进行内存分页（不可为null）
	 * @return OffsetPaging<E> 内存分页后的实例
	 * @throws IllegalArgumentException 偏移量<0或每页大小≤0时抛出
	 */
	public static <E> OffsetPaging<E> of(long offset, int pageSize, List<E> elements) {
		Assert.notNull(elements, "Elements list cannot be null");
		return of(offset, pageSize, (cursor, length) -> {
			int fromIndex = Math.toIntExact(cursor);
			if (fromIndex >= elements.size()) {
				return Collections.emptyList();
			}
			int toIndex = Math.min(Math.addExact(fromIndex, length), elements.size());
			return elements.subList(fromIndex, toIndex);
		}, Function.identity(), (e) -> elements.size());
	}

	/**
	 * 基于偏移量和指定总数创建OffsetPaging实例
	 * 
	 * @param <T>         分页查询结果类型（元素集合）
	 * @param <E>         分页元素类型
	 * @param offset      偏移量，必须≥0（从0开始）
	 * @param pageSize    每页大小，必须>0（不允许为0或负数）
	 * @param offsetQuery 分页查询器：入参为偏移量、每页大小，返回元素集合Collection<E>（不可为null）
	 * @param total       总记录数（可为null，表示未知总数）
	 * @return OffsetPaging<E> 分页实例
	 * @throws IllegalArgumentException 偏移量<0或每页大小≤0时抛出
	 */
	public static <T, E> OffsetPaging<E> of(long offset, int pageSize,
			@NonNull PagingQuery<Long, ? extends Collection<E>> offsetQuery, Long total) {
		return of(offset, pageSize, offsetQuery, Function.identity(), (e) -> total);
	}

	/**
	 * 基于偏移量创建OffsetPaging实例（支持自定义结果映射和总数映射）
	 * 
	 * @param <T>           分页查询的原始结果类型
	 * @param <E>           分页元素类型
	 * @param offset        偏移量，必须≥0（从0开始）
	 * @param pageSize      每页大小，必须>0（不允许为0或负数）
	 * @param offsetQuery   分页查询器：入参为偏移量、每页大小，返回原始查询结果T（不可为null）
	 * @param elementMapper 结果映射器：将原始查询结果T转换为分页元素集合Collection<E>（不可为null）
	 * @param totalMapper   总数映射器：将原始查询结果T转换为总记录数（Number类型，不可为null）
	 * @return OffsetPaging<E> 分页实例
	 * @throws IllegalArgumentException 偏移量<0或每页大小≤0时抛出
	 */
	public static <T, E> OffsetPaging<E> of(long offset, int pageSize,
			@NonNull PagingQuery<Long, ? extends T> offsetQuery,
			@NonNull Function<? super T, ? extends Collection<E>> elementMapper,
			@NonNull Function<? super T, ? extends Number> totalMapper) {
		return new OffsetPaging<>(offset, pageSize, offsetQuery, (e) -> {
			Collection<E> elements = elementMapper.apply(e);
			return elements == null ? null : Listable.forCollection(elements);
		}, totalMapper);
	}

	/**
	 * 基于页码和内存列表创建OffsetPaging实例（内存分页）
	 * 
	 * @param <E>        分页元素类型
	 * @param pageNumber 页码，必须≥1（从1开始）
	 * @param pageSize   每页大小，必须>0（不允许为0或负数）
	 * @param elements   原始数据列表，基于该列表进行内存分页（不可为null）
	 * @return OffsetPaging<E> 内存分页后的实例
	 * @throws IllegalArgumentException 页码≤0或每页大小≤0时抛出
	 */
	public static <E> OffsetPaging<E> ofPageNumber(long pageNumber, int pageSize, List<E> elements) {
		return of(getOffset(pageNumber, pageSize), pageSize, elements);
	}

	/**
	 * 基于页码和指定总数创建OffsetPaging实例
	 * 
	 * @param <T>         分页查询结果类型（元素集合）
	 * @param <E>         分页元素类型
	 * @param pageNumber  页码，必须≥1（从1开始）
	 * @param pageSize    每页大小，必须>0（不允许为0或负数）
	 * @param offsetQuery 分页查询器：入参为偏移量、每页大小，返回元素集合Collection<E>（不可为null）
	 * @param total       总记录数（可为null，表示未知总数）
	 * @return OffsetPaging<E> 分页实例
	 * @throws IllegalArgumentException 页码≤0或每页大小≤0时抛出
	 */
	public static <T, E> OffsetPaging<E> ofPageNumber(long pageNumber, int pageSize,
			@NonNull PagingQuery<Long, ? extends Collection<E>> offsetQuery, Long total) {
		return ofPageNumber(pageNumber, pageSize, offsetQuery, Function.identity(), (e) -> total);
	}

	/**
	 * 基于页码创建OffsetPaging实例（支持自定义结果映射和总数映射）
	 * 
	 * @param <T>             分页查询的原始结果类型
	 * @param <E>             分页元素类型
	 * @param pageNumber      页码，必须≥1（从1开始）
	 * @param pageSize        每页大小，必须>0（不允许为0或负数）
	 * @param pageNumberQuery 分页查询器：入参为页码、每页大小，返回原始查询结果T（不可为null）
	 * @param elementMapper   结果映射器：将原始查询结果T转换为分页元素集合Collection<E>（不可为null）
	 * @param totalMapper     总数映射器：将原始查询结果T转换为总记录数（Number类型，不可为null）
	 * @return OffsetPaging<E> 分页实例
	 * @throws IllegalArgumentException 页码≤0或每页大小≤0时抛出
	 */
	public static <T, E> OffsetPaging<E> ofPageNumber(long pageNumber, int pageSize,
			@NonNull PagingQuery<Long, ? extends T> pageNumberQuery,
			@NonNull Function<? super T, ? extends Collection<E>> elementMapper,
			@NonNull Function<? super T, ? extends Number> totalMapper) {
		return of(getOffset(pageNumber, pageSize), pageSize, (offset, limit) -> {
			return pageNumberQuery.query(getPageNumber(offset, limit), limit);
		}, elementMapper, totalMapper);
	}

	/**
	 * 通用分页构造器（支持自定义结果映射&总数映射）
	 * 
	 * @param <T>           分页查询的原始结果类型
	 * @param offset        偏移量，必须≥0（从0开始）
	 * @param pageSize      每页大小，必须>0（不允许为0或负数）
	 * @param offsetQuery   分页查询器：入参为偏移量、每页大小，返回原始查询结果T（不可为null）
	 * @param elementMapper 结果映射器：将原始查询结果T转换为分页元素列表Listable<V>（不可为null）
	 * @param totalMapper   总数映射器：将原始查询结果T转换为总记录数（Number类型，不可为null）
	 * @throws IllegalArgumentException 偏移量<0或每页大小≤0时抛出
	 */
	public <T> OffsetPaging(long offset, int pageSize, @NonNull PagingQuery<Long, ? extends T> offsetQuery,
			@NonNull Function<? super T, ? extends Listable<V>> elementMapper,
			@NonNull Function<? super T, ? extends Number> totalMapper) {
		super(offset, pageSize, (cursor, length) -> {
			Assert.isTrue(offset >= 0, "Offset must be greater than or equal to 0");
			T result = offsetQuery.query(cursor, length);

			Listable<V> elements = result == null ? Listable.empty() : elementMapper.apply(result);
			elements = elements == null ? Listable.empty() : elements;

			Number totalNumber = result == null ? null : totalMapper.apply(result);
			Long totalCount = totalNumber == null ? null : NumberUtils.toLong(totalNumber);

			Long nextCursor;
			if (totalCount == null) {
				nextCursor = elements.hasElements() ? Math.addExact(cursor, length) : null;
			} else {
				nextCursor = (totalCount - cursor) > length ? Math.addExact(cursor, length) : null;
			}

			return new CursorPage<>(cursor, elements, nextCursor, totalCount);
		});
	}

	@Override
	public Page<Long, V> getCurrentPage() {
		return new Page<>(super.getCurrentPage(), getPageNumber(), getPageSize());
	}

	/**
	 * 获取当前页码
	 * 
	 * @return 当前页码（从1开始）
	 */
	public final long getPageNumber() {
		return getPageNumber(getCurrentCursor(), getPageSize());
	}

	/**
	 * 跳转到指定页码（复用当前每页大小）
	 * 
	 * @param pageNumber 目标页码，必须≥1（从1开始）
	 * @return 新的OffsetPaging实例
	 * @throws IllegalArgumentException 页码≤0时抛出
	 */
	public final OffsetPaging<V> jumpToPage(long pageNumber) {
		return jumpToPage(pageNumber, getPageSize());
	}

	/**
	 * 跳转到指定页码并指定每页大小
	 * 
	 * @param pageNumber 目标页码，必须≥1（从1开始）
	 * @param pageSize   每页大小，必须>0（不允许为0或负数）
	 * @return 新的OffsetPaging实例
	 * @throws IllegalArgumentException 页码≤0或每页大小≤0时抛出
	 */
	public OffsetPaging<V> jumpToPage(long pageNumber, int pageSize) {
		return new OffsetPaging<>(getOffset(pageNumber, pageSize), pageSize, this::query, Function.identity(),
				(e) -> e.isKnownTotal() ? e.getTotalCount() : null);
	}
}